package com.example.supermarketpetproject.detail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.supermarketpetproject.productlist.domain.model.Product

@Composable
fun AddToCartButton(product: Product?, isLoading: Boolean, addToCart: () -> Unit) {
    product?.let {
        if (it.stock > 0) {
            AddToCartButtonWithStock(modifier = Modifier, product = it, isLoading = isLoading, addToCart = addToCart)
        } else {
            AddToCartButtonNoStock()
        }
    }
}