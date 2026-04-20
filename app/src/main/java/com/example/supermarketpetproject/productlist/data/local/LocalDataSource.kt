package com.example.supermarketpetproject.productlist.data.local

import com.example.supermarketpetproject.cart.data.local.database.dao.CartItemDao
import com.example.supermarketpetproject.cart.data.local.database.entity.CartItemEntity
import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.local.database.entity.ProductEntity
import com.example.supermarketpetproject.productlist.data.local.database.entity.PromotionEntity
import com.example.supermarketpetproject.productlist.data.local.database.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)

    fun getProductsByIds(productsIds: Set<String>): Flow<List<ProductEntity>> {
        if (productsIds.isEmpty()) return flowOf(emptyList())
        return productDao.getProductsByIds(productsIds.toList())
    }

    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()
    suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.replaceAllProducts(products)
    }

    suspend fun savePromotions(promotionsEntity: List<PromotionEntity>) {
        promotionDao.replaceAllPromotions(promotionsEntity)
    }

    fun getAllCartItems(): Flow<List<CartItemEntity>> =
        cartItemDao.getAllCartItems()

    suspend fun getCartItemById(productId: String): CartItemEntity? =
        cartItemDao.getCartItemById(productId)

    suspend fun updateCartItem(cartItemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.updateCartItem(cartItemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCartItem(cartItemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.deleteCartItem(cartItemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            cartItemDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCartItem(itemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.insertCartItem(itemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}