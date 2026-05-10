package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.domain.model.AppError
import com.example.supermarketpetproject.core.fakes.FakeCartItemRepository
import com.example.supermarketpetproject.core.fakes.FakeProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddToCartUseCaseTest {
    private val product1 = product {
        withId("product1")
        withStock(7)
    }
    private val product2 = product {
        withId("product2")
        withStock(3)
    }

    @Test
    fun zero_quantity_throws_QuantityMustBePositive() = runTest {
        val useCase = AddToCartUseCase(FakeCartItemRepository(), FakeProductRepository())

        val exception = runCatching { useCase(product1.id, 0) }.exceptionOrNull()

        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun negative_quantity_throws_QuantityMustBePositive() = runTest {
        val useCase = AddToCartUseCase(FakeCartItemRepository(), FakeProductRepository())

        val exception = runCatching { useCase(product2.id, -3) }.exceptionOrNull()

        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun non_existing_product_throws_NotFoundError() = runTest {
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product1))
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = AddToCartUseCase(fakeCartItemRepository, fakeProductRepository)

        val exception = runCatching { useCase(product2.id, 2) }.exceptionOrNull()

        assertTrue(exception is AppError.NotFoundError)
    }

    @Test
    fun insufficient_stock_throws_InsufficientStock() = runTest {
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product1, product2))
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = AddToCartUseCase(fakeCartItemRepository, fakeProductRepository)

        val exception1 = runCatching { useCase(product1.id, 8) }.exceptionOrNull()
        val exception2 = runCatching { useCase(product2.id, 4) }.exceptionOrNull()

        assertTrue(exception1 is AppError.Validation.InsufficientStock && exception2 is AppError.Validation.InsufficientStock)
        assertEquals(7, (exception1 as AppError.Validation.InsufficientStock).available)
        assertEquals(3, (exception2 as AppError.Validation.InsufficientStock).available)
    }

    @Test
    fun succesful_case_adds_item_to_cart() = runTest {
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product1, product2))
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = AddToCartUseCase(fakeCartItemRepository, fakeProductRepository)

        useCase(product1.id, 2)

        val cartItems = fakeCartItemRepository.getCartItems().first()
        assertEquals(1, cartItems.size)
        assertEquals(product1.id, cartItems.first().productId)
        assertEquals(2, cartItems.first().quantity)
    }

    @Test
    fun default_quantity_adds_one_item() = runTest {
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product1, product2))
        }
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = AddToCartUseCase(fakeCartItemRepository, fakeProductRepository)

        useCase(product1.id)

        val cartItems = fakeCartItemRepository.getCartItems().first()
        assertEquals(cartItems.size, 1)
        assertEquals(cartItems.first().productId, product1.id)
        assertEquals(cartItems.first().quantity, 1)
    }
}