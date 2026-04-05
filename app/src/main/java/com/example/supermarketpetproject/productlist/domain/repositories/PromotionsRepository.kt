package com.example.supermarketpetproject.productlist.domain.repositories

import com.example.supermarketpetproject.productlist.domain.model.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionsRepository {
    fun getActivePromotions(): Flow<List<Promotion>>
    suspend fun refreshPromotions()
}