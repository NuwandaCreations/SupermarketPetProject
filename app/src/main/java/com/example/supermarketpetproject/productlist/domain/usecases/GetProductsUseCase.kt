package com.example.supermarketpetproject.productlist.domain.usecases

import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import com.example.supermarketpetproject.productlist.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionsRepository: PromotionsRepository,
    private val settingsRepository: SettingsRepository,
    private val getPromotionsForProductUseCase: GetPromotionsForProductUseCase
) {
    operator fun invoke(): Flow<List<ProductWithPromotion>> {
        return combine(
            productRepository.getProducts(),
            promotionsRepository.getActivePromotions(),
            settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->
            val now = Instant.now()

            val activePromotions = promotions.filter {
                it.startTime <= now && it.endTime >= now
            }

            val stockProducts = if (inStockOnly) {
                products.filter { it.stock > 0 }
            } else {
                products
            }

            stockProducts.map { product ->
                val promotion = getPromotionsForProductUseCase(product, activePromotions)
                ProductWithPromotion(product, promotion)
            }
        }
    }
}