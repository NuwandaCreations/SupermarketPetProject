package com.example.supermarketpetproject.productlist.domain.usecases

import com.example.supermarketpetproject.core.presentation.util.roundTo2Decimals
import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.model.ProductPromotion
import com.example.supermarketpetproject.productlist.domain.model.Promotion
import com.example.supermarketpetproject.productlist.domain.model.PromotionType
import javax.inject.Inject

class GetPromotionsForProductUseCase @Inject constructor() {
    operator fun invoke(product: Product, promotions: List<Promotion>): ProductPromotion? {
        val productPromotions = promotions.filter {
            it.productIds.contains(product.id)
        }

        val percentPromotions =
            productPromotions.filter { it.type == PromotionType.PERCENT }.maxByOrNull { it.value }

        if (percentPromotions != null) {
            val percent = percentPromotions.value.coerceIn(0.0, 100.0)
            val dicsountPrice = (product.price * (1 - percent / 100.0)).roundTo2Decimals()
            return ProductPromotion.Percent(percent, dicsountPrice)
        }

        val buyPayPromotions =
            productPromotions.firstOrNull() { it.type == PromotionType.BUY_X_PAY_Y }
        if (buyPayPromotions != null) {
            val buy = buyPayPromotions.buyQuantity ?: return null
            val pay = buyPayPromotions.value.toInt().coerceIn(0, buy)
            return ProductPromotion.BuyXPayY(buy = buy, pay = pay, label = "${buy} x ${pay}")
        }

        return null
    }
}