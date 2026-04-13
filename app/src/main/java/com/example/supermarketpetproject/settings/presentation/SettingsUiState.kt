package com.example.supermarketpetproject.settings.presentation

import com.example.supermarketpetproject.core.domain.model.ThemeMode

data class SettingsUiState(
    val inStockOnly: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
