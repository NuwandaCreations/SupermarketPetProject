package com.example.supermarketpetproject.productlist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.supermarketpetproject.productlist.domain.model.Promotion
import com.example.supermarketpetproject.productlist.domain.model.PromotionType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

@Entity(tableName = "promotions")
class PromotionEntity(
    @PrimaryKey
    val id: String,
    val productIds: String,
    val type: String,
    val percent: Int? = null,
    val buyX: Int? = null,
    val payY: Int? = null,
    val startAtEpoch: Long,
    val endAtEpoch: Long
)

fun PromotionEntity.toDomain(json: Json): Promotion? {
    val decodePromotionsIds = runCatching {
        json.decodeFromString(
            ListSerializer(String.serializer()),
            productIds
        )
    }.getOrNull()

    val finalType = runCatching {
        PromotionType.valueOf(
            type.trim().uppercase()
        )
    }.getOrNull()

    if (decodePromotionsIds == null || finalType == null) return null

    val finalOfferValue = when (finalType) {
        PromotionType.PERCENT -> percent
        PromotionType.BUY_X_PAY_Y -> payY
    }?.toDouble() ?: return null

    return Promotion(
        id = id,
        productIds = decodePromotionsIds,
        type = finalType,
        value = finalOfferValue,
        startTime = Instant.ofEpochSecond(startAtEpoch),
        endTime = Instant.ofEpochSecond(endAtEpoch)
        )
}