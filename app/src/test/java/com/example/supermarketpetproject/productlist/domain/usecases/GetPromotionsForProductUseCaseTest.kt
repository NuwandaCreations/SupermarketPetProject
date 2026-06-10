package com.example.supermarketpetproject.productlist.domain.usecases

import com.example.supermarketpetproject.core.builders.product
import com.example.supermarketpetproject.core.builders.promotion
import com.example.supermarketpetproject.productlist.domain.model.ProductPromotion
import com.example.supermarketpetproject.productlist.domain.model.PromotionType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class GetPromotionsForProductUseCaseTest {
    private val useCase = GetPromotionsForProductUseCase()

    @Test
    fun given_no_promotions_when_invoke_then_return_null() {
        val product = product()

        val response = useCase(product, emptyList())

        assertNull(response)
    }

    @Test
    fun given_percent_promotion_when_invoke_then_returns_discounted_price_rounded_to_2_decimals() {
        val productId = "product_id"
        val product = product{
            withPrice(10.0)
            withId(productId)
        }
        val promotion = promotion{
            withType(PromotionType.PERCENT)
            withProductIds(listOf(productId))
            withValue(15.0)
        }

        val response = useCase(product, listOf(promotion))

        assertTrue(response is ProductPromotion.Percent)
        response as ProductPromotion.Percent
        assertEquals(8.50, response.discountPrice, 0.001)
        assertEquals(15.0, response.percent, 0.001)
    }
}