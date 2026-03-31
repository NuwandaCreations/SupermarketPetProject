package com.example.supermarketpetproject.productlist.presentation

import com.example.supermarketpetproject.productlist.domain.model.Product

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<String>,
        val selectedCategory: String?,
//        sortOption
    ) : ProductListUiState()
}