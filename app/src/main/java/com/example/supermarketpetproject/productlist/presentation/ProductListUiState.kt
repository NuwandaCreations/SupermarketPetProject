package com.example.supermarketpetproject.productlist.presentation

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
//        val products: List<Product>
//        categories:List<Category>
        val selectedCategory: String,
//        sortOption
    ) : ProductListUiState()
}