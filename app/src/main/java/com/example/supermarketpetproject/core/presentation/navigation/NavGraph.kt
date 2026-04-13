package com.example.supermarketpetproject.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supermarketpetproject.productlist.presentation.ProductListScreen
import com.example.supermarketpetproject.settings.presentation.SettingsScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry(Screen.ProductList) {
            ProductListScreen(navigateToSettings = { backStack.add(Screen.Settings) })
        }
        entry(Screen.Cart) {
            Text("Cart")
        }
        entry(Screen.Settings) {
            SettingsScreen(onBack = { backStack.removeLastOrNull() })
        }
        entry(Screen.ProductDetail(productId = "1")) {
            Text("ProductDetail")
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}