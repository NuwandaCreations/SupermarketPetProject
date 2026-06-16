package com.example.supermarketpetproject.core.fakes

import com.example.supermarketpetproject.productlist.domain.model.Promotion
import com.example.supermarketpetproject.productlist.domain.repositories.PromotionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePromotionsRepository: PromotionsRepository {
    private var _promotions = MutableStateFlow<List<Promotion>>(emptyList())

    fun setPromotions(promotions: List<Promotion>) {
        _promotions.value = promotions
    }

    override fun getActivePromotions(): Flow<List<Promotion>> {
        return _promotions.asStateFlow()
    }

    override suspend fun refreshPromotions() {
    }
}