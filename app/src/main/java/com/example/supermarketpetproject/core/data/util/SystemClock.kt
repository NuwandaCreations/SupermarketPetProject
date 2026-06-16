package com.example.supermarketpetproject.core.data.util

import com.example.supermarketpetproject.core.domain.util.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock @Inject constructor() : Clock {
    override fun now(): Instant = Instant.now()
}