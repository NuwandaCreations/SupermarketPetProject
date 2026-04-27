package com.example.supermarketpetproject.productlist.presentation.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    isFiltersVisible: Boolean = true,
    cartItemsCount: Int,
    onFilterSelected: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit,
    onCartSelected: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Supermercado",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = { onFilterSelected(!isFiltersVisible) }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if (isFiltersVisible) "Ocultar filtros" else "Mostrar filtros",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = { onSettingsSelected() }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = if (isFiltersVisible) "Ocultar configuración" else "Mostrar configuración",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            BadgedBox(
                modifier = Modifier.padding(end = 5.dp),
                badge = {
                    if (cartItemsCount > 0) {
                        Badge(
                            modifier = Modifier.offset(x = (-5).dp, y = 5.dp)
                        ) {
                            Text(
                                text = if (cartItemsCount > 99) "99+" else cartItemsCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }) {
                IconButton(onClick = { onCartSelected() }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = if (isFiltersVisible) "Ocultar carrito" else "Mostrar carrito",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    )
}