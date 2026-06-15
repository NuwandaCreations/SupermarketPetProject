package com.example.supermarketpetproject.core.builders

import com.example.supermarketpetproject.cart.domain.model.CartItem

class CartItemBuilder {
    private var productId: String = "ProductId"
    private var quantity: Int = 1

    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build() = CartItem(productId, quantity)
}

fun cartItem(block: CartItemBuilder.() -> Unit = {}): CartItem = CartItemBuilder().apply(block).build()