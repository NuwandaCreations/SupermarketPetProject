package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.cart.domain.repository.CartItemRepository
import com.example.supermarketpetproject.core.domain.model.AppError
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String, quantity: Int = 1) {
        if (quantity <= 0) throw AppError.Validation.QuantityMustBePositive

        val product = productRepository.getProductById(productId).first() ?: throw AppError.NotFoundError
        val existngItem = cartItemRepository.getCartItemById(productId)
        val newQuantity = (existngItem?.quantity ?: 0) + quantity

        if (newQuantity > product.stock) throw AppError.Validation.InsufficientStock(product.stock)

        cartItemRepository.addToCart(productId, quantity)
    }
}