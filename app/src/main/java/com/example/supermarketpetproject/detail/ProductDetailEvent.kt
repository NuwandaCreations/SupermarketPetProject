package com.example.supermarketpetproject.detail

sealed interface ProductDetailEvent {
    data class ShowMessage(val message: String) : ProductDetailEvent
    data class ShowError(val message: String) : ProductDetailEvent
}