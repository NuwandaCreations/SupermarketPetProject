package com.example.supermarketpetproject.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.supermarketpetproject.productlist.presentation.ProductListScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry(Screen.ProductList) {
            ProductListScreen()
        }
        entry(Screen.Cart) {
            Text("Cart")
        }
        entry(Screen.Settings) {
            Text("Settings")
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