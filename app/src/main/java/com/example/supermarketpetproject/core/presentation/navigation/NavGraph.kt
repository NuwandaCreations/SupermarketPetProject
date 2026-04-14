package com.example.supermarketpetproject.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supermarketpetproject.detail.presentation.ProductDetailScreen
import com.example.supermarketpetproject.productlist.presentation.ProductListScreen
import com.example.supermarketpetproject.settings.presentation.SettingsScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry(Screen.ProductList) {
            ProductListScreen(
                navigateToSettings = { backStack.add(Screen.Settings) },
                navigateToProductDetail = { productId ->
                    backStack.add(Screen.ProductDetail(productId))
                }
            )
        }
        entry(Screen.Cart) {
            Text("Cart")
        }
        entry(Screen.Settings) {
            SettingsScreen(onBack = { backStack.removeLastOrNull() })
        }
        entry<Screen.ProductDetail> { route ->
            ProductDetailScreen(
                productId = route.productId, onBack = { backStack.removeLastOrNull() })
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}