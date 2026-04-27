package com.example.supermarketpetproject.cart.domain.model

data class CartSummary(
    val subtotal: Double,
    val discountTotal: Double,
    val finalTotal: Double
)