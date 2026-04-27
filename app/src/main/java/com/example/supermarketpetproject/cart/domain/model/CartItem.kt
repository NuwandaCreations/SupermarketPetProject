package com.example.supermarketpetproject.cart.domain.model

import com.example.supermarketpetproject.cart.data.local.database.entity.CartItemEntity

data class CartItem(
    val productId: String,
    val quantity: Int
)

fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        productId = productId,
        quantity = quantity
    )
}
