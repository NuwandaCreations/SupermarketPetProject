package com.example.supermarketpetproject.cart.presentation.model

import com.example.supermarketpetproject.cart.domain.model.CartItem
import com.example.supermarketpetproject.productlist.domain.model.Product
import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val item: ProductWithPromotion
)
