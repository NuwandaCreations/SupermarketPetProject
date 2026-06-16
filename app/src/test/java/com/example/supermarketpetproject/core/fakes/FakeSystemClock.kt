package com.example.supermarketpetproject.core.fakes

import com.example.supermarketpetproject.core.domain.util.Clock
import java.time.Instant

class FakeSystemClock(private var currentTime: Instant = Instant.now()) : Clock {
    fun setTime(time: Instant) {
        currentTime = time
    }

    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }

    override fun now(): Instant = currentTime
}