package com.example.supermarketpetproject.core.domain.util

import java.time.Instant

interface Clock {
    fun now(): Instant
}