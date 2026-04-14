package com.example.supermarketpetproject.productlist.data.local

import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.local.database.entity.ProductEntity
import com.example.supermarketpetproject.productlist.data.local.database.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getProductById(productId: String): Flow<ProductEntity?> =
        productDao.getProductById(productId)
    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()
    suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.replaceAllProducts(products)
    }

    suspend fun savePromotions(promotionsEntity: List<PromotionEntity>) {
        promotionDao.replaceAllPromotions(promotionsEntity)
    }
}