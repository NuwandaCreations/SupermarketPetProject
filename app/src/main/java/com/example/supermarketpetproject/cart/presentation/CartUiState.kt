package com.example.supermarketpetproject.cart.presentation

import com.example.supermarketpetproject.cart.domain.model.CartSummary
import com.example.supermarketpetproject.cart.presentation.model.CartItemWithPromotion

sealed class CartUiState {
    data class Success(
        val cartItems: List<CartItemWithPromotion>,
        val summary: CartSummary? = null,
        val isLoading: Boolean
    ) : CartUiState()

    data object Loading : CartUiState()
}
