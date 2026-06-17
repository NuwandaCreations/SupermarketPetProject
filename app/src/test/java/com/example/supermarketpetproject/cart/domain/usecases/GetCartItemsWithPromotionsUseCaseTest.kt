package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.core.builders.cartItem
import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.builders.promotion
import com.example.supermarketpetproject.core.fakes.FakeCartItemRepository
import com.example.supermarketpetproject.core.fakes.FakeProductRepository
import com.example.supermarketpetproject.core.fakes.FakePromotionsRepository
import com.example.supermarketpetproject.core.fakes.FakeSystemClock
import com.example.supermarketpetproject.productlist.domain.usecases.GetPromotionsForProductUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCartItemsWithPromotionsUseCaseTest {
    private val now = FakeSystemClock().fakeNow()
    private val clock = FakeSystemClock().apply { fakeNow() }

    private fun useCase(
        cartItemRepository: FakeCartItemRepository = FakeCartItemRepository(),
        productRepository: FakeProductRepository = FakeProductRepository(),
        promotionsRepository: FakePromotionsRepository = FakePromotionsRepository(),
        clock: FakeSystemClock = this.clock
    ) = GetCartItemsWithPromotionsUseCase(
        cartItemRepository,
        productRepository,
        promotionsRepository,
        GetPromotionsForProductUseCase(),
        clock
    )

    @Test
    fun `given empty cart, when invoke, then return empty list`() = runTest {
        val cartRepository = FakeCartItemRepository().apply { setCartItems(emptyList()) }

        val result = (useCase(cartItemRepository = cartRepository)()).first()

        assert(result.isEmpty())
    }

    @Test
    fun `given cart item with active promotion, when invoke, then returns item with promotion`() =
        runTest {
            val productId = "product-id"
            val product = product {
                withId(productId)
            }
            val promotion = promotion {
                withProductIds(listOf(productId))
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(50))
            }
            val cartItem = cartItem {
                withProductId("product-id")
                withQuantity(2)
            }
            val cartRepository = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }
            val promotionsRepository =
                FakePromotionsRepository().apply { setPromotions(listOf(promotion)) }
            val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

            val result = useCase(
                productRepository = productRepository,
                cartItemRepository = cartRepository,
                promotionsRepository = promotionsRepository
            )().first()

            assertEquals(1, result.size)
            assertNotNull(result.first().item.promotion)
            assertEquals(cartItem, result.first().cartItem)
        }

    @Test
    fun `given cart item without matching product, when invoke, then skip item`() = runTest {
        val cartRepository =
            FakeCartItemRepository().apply { setCartItems(listOf(cartItem { withProductId("ghost-id") })) }
        val productRepository = FakeProductRepository().apply { setProducts(emptyList()) }

        val result = useCase(
            productRepository = productRepository,
            cartItemRepository = cartRepository
        )().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `given promotion ending now, when invoke, then item should be include`() = runTest {
        val productId = "product-id"
        val product = product {
            withId(productId)
        }
        val endingPromotion = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now)
        }
        val cartItem = cartItem {
            withProductId("product-id")
        }
        val cartRepository = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }
        val promotionsRepository =
            FakePromotionsRepository().apply { setPromotions(listOf(endingPromotion)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

        val result = useCase(
            productRepository = productRepository,
            cartItemRepository = cartRepository,
            promotionsRepository = promotionsRepository
        )().first()

        assertEquals(1, result.size)
        assertNotNull(result.first().item.promotion)
    }

    @Test
    fun `given expired promotion, when invoke, then item remains without promotion`() = runTest {
        val productId = "product-id"
        val product = product {
            withId(productId)
        }
        val endedPromotion = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now.minusSeconds(1))
        }
        val cartItem = cartItem {
            withProductId("product-id")
        }
        val cartRepository = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }
        val promotionsRepository =
            FakePromotionsRepository().apply { setPromotions(listOf(endedPromotion)) }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

        val result = useCase(
            productRepository = productRepository,
            cartItemRepository = cartRepository,
            promotionsRepository = promotionsRepository
        )().first()

        assertNull(result.first().item.promotion)
        assertEquals(product, result.first().item.product)
    }

    @Test
    fun `given active promotion, when time advances, then flow emits update list without promotion`() =
        runTest {
            val productId = "product-id"
            val product = product {
                withId(productId)
            }
            val promotion = promotion {
                withProductIds(listOf(productId))
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(5))
            }
            val cartItem = cartItem {
                withProductId("product-id")
            }
            val cartRepository = FakeCartItemRepository().apply { setCartItems(listOf(cartItem)) }
            val promotionsRepository =
                FakePromotionsRepository().apply { setPromotions(listOf(promotion)) }
            val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }

            val myUseCase = useCase(
                productRepository = productRepository,
                cartItemRepository = cartRepository,
                promotionsRepository = promotionsRepository
            )()

            val firstResult = myUseCase.first()
            assertNotNull(firstResult.first().item.promotion)

            clock.advanceTime(6)
            val secondResult = myUseCase.first()
            assertNull(secondResult.first().item.promotion)
        }
}