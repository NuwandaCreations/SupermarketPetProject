package com.example.supermarketpetproject.productlist.data.local

import com.example.supermarketpetproject.productlist.data.local.database.dao.ProductDao
import com.example.supermarketpetproject.productlist.data.local.database.dao.PromotionDao
import com.example.supermarketpetproject.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {
    fun getAlProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.replaceAllProducts(products)
    }
}