package com.example.supermarketpetproject.core.builders

import com.example.supermarketpetproject.productlist.domain.model.Promotion
import com.example.supermarketpetproject.productlist.domain.model.PromotionType
import java.time.Instant

class PromotionBuilder {
    private var id: String = "PromotionId"
    private var type: PromotionType = PromotionType.PERCENT
    private var productIds: List<String> = listOf("ProductId")
    private var value: Double = 10.0
    private var buyQuantity: Int? = null
    private var startTime: Instant = Instant.now().minusSeconds(3600)
    private var endTime: Instant = Instant.now().plusSeconds(3600)

    fun withId(id: String) = apply { this.id = id }
    fun withType(type: PromotionType) = apply { this.type = type }
    fun withProductIds(productIds: List<String>) = apply { this.productIds = productIds }
    fun withValue(value: Double) = apply { this.value = value}
    fun withBuyQuantity(buyQuantity: Int) = apply { this.buyQuantity = buyQuantity }
    fun withStartTime(startTime: Instant) = apply { this.startTime = startTime }
    fun withEndTime(endTime: Instant) = apply { this.endTime = endTime }

    fun build() = Promotion(id, type, productIds, value, buyQuantity, startTime, endTime)
}

fun promotion(block: PromotionBuilder.() -> Unit = {}): Promotion = PromotionBuilder().apply(block).build()