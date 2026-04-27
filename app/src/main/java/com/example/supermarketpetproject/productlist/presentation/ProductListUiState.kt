package com.example.supermarketpetproject.productlist.presentation

import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion
import com.example.supermarketpetproject.productlist.domain.model.SortOption

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
        val products: List<ProductWithPromotion>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOption
    ) : ProductListUiState()
}