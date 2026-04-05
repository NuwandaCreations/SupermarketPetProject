package com.example.supermarketpetproject.productlist.data.remote.response

import com.example.supermarketpetproject.productlist.data.local.database.entity.PromotionEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Serializable
data class PromotionsResponse(
    @SerialName("promotions") val promotions: List<PromotionResponse>
)

@Serializable
data class PromotionResponse(
    @SerialName("id") val id: String,
    @SerialName("productId") val productId: String,
    @SerialName("type") val type: String,
    @SerialName("percent") val percent: Int? = null,
    @SerialName("buyX") val buyX: Int? = null,
    @SerialName("payY") val payY: Int? = null,
    @SerialName("startAtEpoch") val startAtEpoch: Long? = null,
    @SerialName("endAtEpoch") val endAtEpoch: Long? = null
)

fun PromotionResponse.toEntity(json: Json): PromotionEntity? {
    if (startAtEpoch == null || endAtEpoch == null) return null
    val productIds = listOf(productId)
    val productIdsJson = json.encodeToString(ListSerializer(String.serializer()), productIds)
    return PromotionEntity(
        id = id,
        productIds = productIdsJson,
        type = type,
        percent = percent,
        buyX = buyX,
        payY = payY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch
    )
}