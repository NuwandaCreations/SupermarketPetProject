package com.example.supermarketpetproject.cart.presentation.model

import com.example.supermarketpetproject.cart.domain.model.CartItem
import com.example.supermarketpetproject.productlist.domain.model.Product

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val product: Product
)
