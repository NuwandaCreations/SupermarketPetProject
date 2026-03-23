package com.example.supermarketpetproject.productlist.data.repositories

import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImp: ProductRepository {
    override fun getProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProduct() {
        TODO("Not yet implemented")
    }
}