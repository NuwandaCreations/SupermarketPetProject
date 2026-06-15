package com.example.supermarketpetproject.cart.domain.ex

import com.example.supermarketpetproject.core.builders.promotion
import com.example.supermarketpetproject.productlist.domain.model.Promotion
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class PromotionsExtensionsTest {
    private val now = Instant.parse("2026-04-03T10:00:00Z")
    @Test
    fun givenFuturePromotion_whenActiveAt_thenExclude() {
        val futurePromotion = promotion {
            withStartTime(now.plusSeconds(10))
            withEndTime(now.plusSeconds(50))
        }
        val promotions = listOf(futurePromotion)

        val result = promotions.activeAt(now)

        assertEquals(0, result.size)
    }

    @Test
    fun givenExpiredPromotion_whenActiveAt_thenExclude() {
        val expiredPromotion = promotion {
            withStartTime(now.minusSeconds(500))
            withEndTime(now.minusSeconds(100))
        }
        val promotions = listOf(expiredPromotion)

        val result = promotions.activeAt(now)

        assertEquals(0, result.size)
    }

    @Test
    fun givenOnGoingPromotion_whenActiveAt_thenInclude() {
        val activePromotion = promotion {
            withStartTime(now.minusSeconds(10))
            withEndTime(now.plusSeconds(50))
        }
        val promotions = listOf(activePromotion)

        val result = promotions.activeAt(now)

        assertEquals(1, result.size)
    }

    @Test
    fun givenExactStartTimePromotion_whenActiveAt_thenInclude() {
        val activePromotion = promotion {
            withStartTime(now)
            withEndTime(now.plusSeconds(50))
        }
        val promotions = listOf(activePromotion)

        val result = promotions.activeAt(now)

        assertEquals(1, result.size)
    }

    @Test
    fun givenExactEndTimePromotion_whenActiveAt_thenInclude() {
        val activePromotion = promotion {
            withStartTime(now.minusSeconds(50))
            withEndTime(now)
        }
        val promotions = listOf(activePromotion)

        val result = promotions.activeAt(now)

        assertEquals(1, result.size)
    }

    @Test
    fun givenEmptyList_whenActiveAt_thenReturnEmpty() {
        val promotions = emptyList<Promotion>()

        val result = promotions.activeAt(now)

        assertEquals(0, result.size)
    }
}