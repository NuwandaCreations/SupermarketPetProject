package com.example.supermarketpetproject.detail.domain.usecases

import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import com.example.supermarketpetproject.productlist.domain.usecases.GetPromotionsForProductUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject
import kotlin.collections.filter

class GetProductDetailWithPromotionUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionsRepository: PromotionsRepository,
    private val getPromotionForProduct: GetPromotionsForProductUseCase
){
    operator fun invoke(productId: String) : Flow<ProductWithPromotion?> {
        return combine(
            productRepository.getProductById(productId),
            promotionsRepository.getActivePromotions()
        ) { product, promotions ->
            val now = Instant.now()
            val activePromotions = promotions.filter {
                it.startTime <= now && it.endTime >= now
            }

            product?.let {
                val finalPromotion = getPromotionForProduct(it, activePromotions)
                ProductWithPromotion(product = it, promotion = finalPromotion)
            }
        }
    }
}