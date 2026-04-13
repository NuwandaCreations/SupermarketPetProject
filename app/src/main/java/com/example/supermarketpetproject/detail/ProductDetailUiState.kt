package com.example.supermarketpetproject.detail

import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion

data class ProductDetailUiState(
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true
)
