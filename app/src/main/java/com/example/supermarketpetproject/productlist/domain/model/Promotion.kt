package com.example.supermarketpetproject.productlist.domain.model

data class Promotion(
    val id: String,
    val productId: String,
    val type: String,
    val percent: Int? = null,
    val buyX: Int? = null,
    val payY: Int? = null,
    val startAtEpoch: Long? = null,
    val endAtEpoch: Long? = null
)
