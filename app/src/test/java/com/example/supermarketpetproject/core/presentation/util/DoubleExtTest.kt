package com.example.supermarketpetproject.core.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtTest {
    @Test
    fun roundTo2Decimals_roundCorrectly() {
        assertEquals(123.46, 123.456789.roundTo2Decimals(), 0.0)
        assertEquals(3.44, 3.4442139.roundTo2Decimals(), 0.0)
    }
}