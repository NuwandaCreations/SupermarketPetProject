package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.core.builders.cartItem
import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.domain.model.AppError
import com.example.supermarketpetproject.core.fakes.FakeCartItemRepository
import com.example.supermarketpetproject.core.fakes.FakeProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCartItemUseCaseTest {
    val productId = "product_id"

    @Test
    fun given_negative_quantity_when_invoke_then_throws_quantity_must_be_positive() = runTest {
        val fakeProductRepository = FakeProductRepository()
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)
        val quantity = -1

        val exception = runCatching { useCase(productId, quantity) }.exceptionOrNull()

        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun given_zero_quantity_when_invoke_then_removes_from_cart() = runTest {
        val product = product {
            withId(productId)
        }
        val cartItemProduct = cartItem {
            withProductId(productId)
            withQuantity(3)
        }
        val quantity = 0
        val fakeProductRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val fakeCartItemRepository =
            FakeCartItemRepository().apply { setCartItems(listOf(cartItemProduct)) }
        val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)

        useCase(productId, quantity)

        val cartItems = fakeCartItemRepository.getCartItems().first()
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun given_missing_product_when_invoke_then_throws_not_found() = runTest {
        val fakeProductRepository = FakeProductRepository().apply { setProducts(emptyList()) }
        val fakeCartItemRepository = FakeCartItemRepository()
        val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)

        val exception = runCatching { useCase(productId, 1) }.exceptionOrNull()

        assertTrue(exception is AppError.NotFoundError)
    }

    @Test
    fun given_requested_quantity_greater_than_stock_when_invoke_then_throws_inssuficient_stock() =
        runTest {
            val product = product {
                withId(productId)
                withStock(3)
            }
            val cartItemProduct = cartItem {
                withProductId(productId)
                withQuantity(1)
            }
            val fakeProductRepository =
                FakeProductRepository().apply { setProducts(listOf(product)) }
            val fakeCartItemRepository =
                FakeCartItemRepository().apply { setCartItems(listOf(cartItemProduct)) }
            val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)

            val exception = runCatching { useCase(productId, 5) }.exceptionOrNull()

            assertTrue(exception is AppError.Validation.InsufficientStock)
        }

    @Test
    fun given_valid_product_and_quantity_when_invoke_then_updates_cart_item() = runTest {
        val product = product {
            withId(productId)
            withStock(20)
        }
        val cartItemProduct = cartItem {
            withProductId(productId)
            withQuantity(1)
        }
        val fakeProductRepository =
            FakeProductRepository().apply { setProducts(listOf(product)) }
        val fakeCartItemRepository =
            FakeCartItemRepository().apply { setCartItems(listOf(cartItemProduct)) }
        val useCase = UpdateCartItemUseCase(fakeCartItemRepository, fakeProductRepository)

        useCase(productId, 7)

        val cartItems = fakeCartItemRepository.getCartItems().first()
        assertTrue(1 == cartItems.size)
        assertTrue(7 == cartItems.first().quantity)
    }
}