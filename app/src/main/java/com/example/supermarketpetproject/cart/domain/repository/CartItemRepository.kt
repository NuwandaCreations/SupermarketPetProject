package com.example.supermarketpetproject.cart.domain.repository

import com.example.supermarketpetproject.cart.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartItemRepository {
    fun getCartItems(): Flow<List<CartItem>>

    suspend fun addToCart(productId: String, quantity: Int)

    suspend fun removeFromCart(productId: String)

    suspend fun updateQuantity(productId: String)

    suspend fun clearCart()
}