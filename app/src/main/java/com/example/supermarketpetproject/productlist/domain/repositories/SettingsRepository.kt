package com.example.supermarketpetproject.productlist.domain.repositories

import com.example.supermarketpetproject.core.domain.model.ThemeMode
import com.example.supermarketpetproject.productlist.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val inStockOnly: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val selectedCategory: Flow<String?>
    val filtersVisible: Flow<Boolean>
    val sortOption: Flow<SortOption>

    suspend fun setInStockOnly(inStockOnly: Boolean)
    suspend fun setThemeMode(value: ThemeMode)
    suspend fun setSelectedCategory(value: String?)
    suspend fun setFiltersVisible(value: Boolean)
    suspend fun setSortOption(value: SortOption)
}