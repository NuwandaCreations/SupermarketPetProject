package com.example.supermarketpetproject.core.fakes

import com.example.supermarketpetproject.core.domain.util.Clock
import java.time.Instant

class FakeSystemClock(private var currentTime: Instant = Instant.now()) : Clock {
    fun fakeNow(): Instant {
        val fakeNow = Instant.parse("2027-01-01T00:00:00.00Z")
        currentTime = fakeNow
        return fakeNow
    }
    fun setTime(time: Instant) {
        currentTime = time
    }

    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }

    override fun now(): Instant = currentTime
}