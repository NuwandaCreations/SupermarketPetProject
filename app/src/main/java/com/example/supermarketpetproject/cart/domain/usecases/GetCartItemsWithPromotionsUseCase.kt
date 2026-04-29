package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.cart.domain.ex.activeAt
import com.example.supermarketpetproject.cart.domain.repository.CartItemRepository
import com.example.supermarketpetproject.cart.presentation.model.CartItemWithPromotion
import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import com.example.supermarketpetproject.productlist.domain.usecases.GetPromotionsForProductUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject

class GetCartItemsWithPromotionsUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val promotionsRepository: PromotionsRepository,
    private val getPromotionForProduct: GetPromotionsForProductUseCase
)
{
    operator fun invoke(): Flow<List<CartItemWithPromotion>> {
        return cartItemRepository.getCartItems().flatMapLatest { cartItems ->
            val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(
                    productRepository.getProductsByIds(ids),
                    promotionsRepository.getActivePromotions()
                ) { products, promotions ->

                    val now = Instant.now()
                    val activePromotions = promotions.activeAt(now)
                    val productsById = products.associateBy { it.id }
                    cartItems.mapNotNull { cartItem ->
                        val product = productsById[cartItem.productId] ?: return@mapNotNull null
                        val promotion = getPromotionForProduct(product, activePromotions)
                        val productWithPromotion = ProductWithPromotion(product, promotion)
                        CartItemWithPromotion(cartItem, productWithPromotion)
                    }
                }
            }
        }
    }
}