package com.example.supermarketpetproject.productlist.domain.usecases

import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor( private val repository: ProductRepository) {
    operator fun invoke() : Flow<List<Product>> {
        return repository.getProducts()
    }
}