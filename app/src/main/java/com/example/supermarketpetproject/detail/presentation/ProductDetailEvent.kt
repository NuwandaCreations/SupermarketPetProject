package com.example.supermarketpetproject.detail.presentation

sealed interface ProductDetailEvent {
    data object UNKOWN_ERROR : ProductDetailEvent
    data object NETWORK_ERROR : ProductDetailEvent
    data object INSUFFICIENT_STOCK_ERROR: ProductDetailEvent
}