package com.example.supermarketpetproject.productlist.presentation

sealed interface ProductListEvent {
    data class ShowMessage(val message: String) : ProductListEvent
}