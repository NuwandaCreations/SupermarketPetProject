package com.example.supermarketpetproject.productlist.domain.repositories

import com.example.supermarketpetproject.productlist.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    fun getProductsByIds(ids: Set<String>): Flow<List<Product>>
    suspend fun refreshProduct()
}