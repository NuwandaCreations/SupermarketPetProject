package com.example.supermarketpetproject.cart.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.supermarketpetproject.cart.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int
)

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        productId = productId,
        quantity = quantity
    )
}