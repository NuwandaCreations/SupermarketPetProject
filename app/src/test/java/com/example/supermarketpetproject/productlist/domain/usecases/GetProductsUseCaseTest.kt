package com.example.supermarketpetproject.productlist.domain.usecases

import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.builders.promotion
import com.example.supermarketpetproject.core.fakes.FakeProductRepository
import com.example.supermarketpetproject.core.fakes.FakePromotionsRepository
import com.example.supermarketpetproject.core.fakes.FakeSettingsRepository
import com.example.supermarketpetproject.core.fakes.FakeSystemClock
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.Instant

class GetProductsUseCaseTest {
    private fun useCase(
        products: FakeProductRepository = FakeProductRepository(),
        promotions: FakePromotionsRepository = FakePromotionsRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()
    ) = GetProductsUseCase(products, promotions, settings, GetPromotionsForProductUseCase(), clock)

    @Test
    fun `given ending now promotion when invoke then it should be included`() = runTest {
        val now = Instant.parse("2027-01-01T00:00:00.00Z")
        val clock = FakeSystemClock().apply { setTime(now) }
        val productId = "product-id"
        val product = product {
            withId(productId)
        }
        val promotion = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(10))
            withEndTime(now)
        }
        val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
        val promotionsRepository =
            FakePromotionsRepository().apply { setPromotions(listOf(promotion)) }

        val response = (useCase(
            products = productRepository,
            promotions = promotionsRepository,
            clock = clock
        )()).first()

        assertNotNull(response.first())
        assertEquals(product, response.first().product)
    }

    @Test
    fun `given active promotion when time advances then promotion shoulg not be longer returned`() =
        runTest {
            val now = Instant.parse("2027-01-01T00:00:00.00Z")
            val clock = FakeSystemClock().apply { setTime(now) }
            val productId = "product-id"
            val product = product {
                withId(productId)
            }
            val promotion = promotion {
                withProductIds(listOf(productId))
                withStartTime(now)
                withEndTime(now.plusSeconds(50))
            }
            val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
            val promotionsRepository =
                FakePromotionsRepository().apply { setPromotions(listOf(promotion)) }

            val firstResult = (useCase(
                products = productRepository,
                promotions = promotionsRepository,
                clock = clock
            )()).first()
            clock.advanceTime(51)
            val secondResult = (useCase(
                products = productRepository,
                promotions = promotionsRepository,
                clock = clock
            )()).first()

            assertNotNull(firstResult.first().promotion)
            assertNull(secondResult.first().promotion)
        }

    @Test
    fun `given inStockOnly enabled when product goes out of stock then it should not be returned`() =
        runTest {
            val productId = "product-id"
            val product = product {
                withId(productId)
                withStock(0)
            }
            val settingsRepository = FakeSettingsRepository().apply { setInStockOnly(true) }
            val productRepository = FakeProductRepository().apply { setProducts(listOf(product)) }
            val myUseCase = useCase(products = productRepository, settings = settingsRepository)

            val response = myUseCase().first()

            assertTrue(response.isEmpty())
        }
}