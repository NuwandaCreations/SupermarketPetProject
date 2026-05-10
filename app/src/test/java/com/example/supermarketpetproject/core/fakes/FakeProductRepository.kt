package com.example.supermarketpetproject.core.fakes

import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeProductRepository : ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())

    fun setProducts(products: List<Product>) {
        _products.value = products
    }

    override fun getProducts(): Flow<List<Product>> = _products.asStateFlow()

    override fun getProductById(id: String): Flow<Product?> {
        return _products.asStateFlow().map { products ->
            products.find { it.id == id }
        }
    }

    override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> {
        return _products.asStateFlow().map { products ->
            products.filter { ids.contains(it.id) }
        }
    }

    override suspend fun refreshProduct() {}
}