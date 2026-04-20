package com.example.supermarketpetproject.cart.data.repository

import com.example.supermarketpetproject.cart.data.local.database.entity.toDomain
import com.example.supermarketpetproject.cart.domain.model.CartItem
import com.example.supermarketpetproject.cart.domain.model.toEntity
import com.example.supermarketpetproject.cart.domain.repository.CartItemRepository
import com.example.supermarketpetproject.core.domain.model.AppError
import com.example.supermarketpetproject.productlist.data.local.LocalDataSource
import com.example.supermarketpetproject.productlist.data.local.database.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartItemRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : CartItemRepository {
    override fun getCartItems(): Flow<List<CartItem>> {
        return localDataSource.getAllCartItems()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val existingItem = localDataSource.getCartItemById(productId)
        if (existingItem != null) {
            val updateItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            localDataSource.updateCartItem(updateItem)
        } else {
            localDataSource.insertCartItem(CartItem(productId, quantity).toEntity())
        }
    }

    override suspend fun removeFromCart(productId: String) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.deleteCartItem(item)
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val item = localDataSource.getCartItemById(productId) ?: throw AppError.NotFoundError
        localDataSource.updateCartItem(item.copy(quantity = quantity))
    }

    override suspend fun clearCart() {
        localDataSource.clearCart()
    }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return localDataSource.getCartItemById(productId)?.toDomain()
    }
}