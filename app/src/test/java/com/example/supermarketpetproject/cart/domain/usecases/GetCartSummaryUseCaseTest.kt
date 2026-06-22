package com.example.supermarketpetproject.cart.domain.usecases

import com.example.supermarketpetproject.core.builders.cartItem
import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.builders.promotion
import com.example.supermarketpetproject.core.fakes.FakeCartItemRepository
import com.example.supermarketpetproject.core.fakes.FakeProductRepository
import com.example.supermarketpetproject.core.fakes.FakePromotionsRepository
import com.example.supermarketpetproject.core.fakes.FakeSystemClock
import com.example.supermarketpetproject.productlist.domain.model.PromotionType
import com.example.supermarketpetproject.productlist.domain.usecases.GetPromotionsForProductUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetCartSummaryUseCaseTest {
    private lateinit var clock: FakeSystemClock
    private lateinit var cartItemRepository: FakeCartItemRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionsRepository: FakePromotionsRepository

    @Before
    fun setup() {
        clock = FakeSystemClock().apply { setTime(Instant.parse("2023-01-01T00:00:00Z")) }
        cartItemRepository = FakeCartItemRepository()
        productRepository = FakeProductRepository()
        promotionsRepository = FakePromotionsRepository()
    }

    private fun useCase() = GetCartSummaryUseCase(
        cartItemRepository,
        productRepository,
        promotionsRepository,
        GetPromotionsForProductUseCase(),
        clock
    )

    @Test
    fun `given 3 items in 2x1 promotion when invoke then discounts 1 unit`() = runTest {
        val productId = "product-id"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }
        val promotion = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.BUY_X_PAY_Y)
            withBuyQuantity(2)
            withValue(1.0)
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(10))
        }
        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(3)
        }

        productRepository.setProducts(listOf(product))
        promotionsRepository.setPromotions(listOf(promotion))
        cartItemRepository.setCartItems(listOf(cartItem))

        val summary = (useCase()()).first()

        assertEquals(300.0, summary.subtotal, 0.0)
        assertEquals(200.0, summary.finalTotal, 0.0)
        assertEquals(100.0, summary.discountTotal, 0.0)
    }

    @Test
    fun `given percent promotion when invoke then return correct summary`() = runTest {
        val productId = "product-id"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }
        val promotion = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(clock.now().minusSeconds(10))
            withEndTime(clock.now().plusSeconds(10))
        }
        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(2)
        }

        productRepository.setProducts(listOf(product))
        promotionsRepository.setPromotions(listOf(promotion))
        cartItemRepository.setCartItems(listOf(cartItem))

        val summary = (useCase()()).first()

        assertEquals(200.0, summary.subtotal, 0.0)
        assertEquals(20.0, summary.discountTotal, 0.0)
        assertEquals(180.0, summary.finalTotal, 0.0)
    }

    @Test
    fun `given multiple products with different promotions when invoke then return sums all correctly`() =
        runTest {
            val now = clock.now()
            val product1 = product {
                withId("product1")
                withPrice(100.0)
            }
            val product2 = product {
                withId("product2")
                withPrice(50.0)
            }
            val promotionPercent = promotion {
                withProductIds(listOf("product1"))
                withType(PromotionType.PERCENT)
                withValue(10.0)
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(10))
            }
            val cart = listOf(
                cartItem {
                    withProductId("product1")
                    withQuantity(1)
                },
                cartItem {
                    withProductId("product2")
                    withQuantity(1)
                }
            )
            productRepository.setProducts(listOf(product1, product2))
            promotionsRepository.setPromotions(listOf(promotionPercent))
            cartItemRepository.setCartItems(cart)

            val summary = useCase()().first()

            assertEquals(150.0, summary.subtotal, 0.0)
            assertEquals(10.0, summary.discountTotal, 0.0)
            assertEquals(140.0, summary.finalTotal, 0.0)
        }

    @Test
    fun `given expired promotion when invoke then discount is zero`() = runTest {
        val now = clock.now()
        val productId = "product-id"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }
        val promotion = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.minusSeconds(1))
        }
        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(1)
        }

        productRepository.setProducts(listOf(product))
        promotionsRepository.setPromotions(listOf(promotion))
        cartItemRepository.setCartItems(listOf(cartItem))

        val summary = useCase()().first()

        assertEquals(100.0, summary.subtotal, 0.0)
        assertEquals(0.0, summary.discountTotal, 0.0)
        assertEquals(100.0, summary.finalTotal, 0.0)
    }

    @Test
    fun `given active promotion when time advances then summary updates`() = runTest {
        val now = clock.now()
        val productId = "product-id"
        val product = product {
            withId(productId)
            withPrice(100.0)
        }
        val promotion = promotion {
            withProductIds(listOf(productId))
            withType(PromotionType.PERCENT)
            withValue(10.0)
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(1))
        }
        val cartItem = cartItem {
            withProductId(productId)
            withQuantity(1)
        }

        productRepository.setProducts(listOf(product))
        promotionsRepository.setPromotions(listOf(promotion))
        cartItemRepository.setCartItems(listOf(cartItem))

        val firstSummary = useCase()().first()
        assertEquals(100.0, firstSummary.subtotal, 0.0)
        assertEquals(10.0, firstSummary.discountTotal, 0.0)
        assertEquals(90.0, firstSummary.finalTotal, 0.0)

        clock.advanceTime(6)

        val secondSummary = useCase()().first()
        assertEquals(100.0, secondSummary.subtotal, 0.0)
        assertEquals(0.0, secondSummary.discountTotal, 0.0)
        assertEquals(100.0, secondSummary.finalTotal, 0.0)
    }
}
