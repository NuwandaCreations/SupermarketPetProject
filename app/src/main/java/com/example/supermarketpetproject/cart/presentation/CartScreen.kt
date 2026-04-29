package com.example.supermarketpetproject.cart.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.supermarketpetproject.cart.presentation.model.CartItemWithPromotion
import com.example.supermarketpetproject.core.presentation.components.MarketTopAppBar
import com.example.supermarketpetproject.core.presentation.components.QuantitySelector
import com.example.supermarketpetproject.productlist.domain.model.ProductPromotion
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    onBack: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        cartViewModel.event.collect { event ->
            when (event) {
                is CartEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MarketTopAppBar(title = "Carrito") { onBack() }
        }
    ) { padding ->
        when (val state = uiState) {
            is CartUiState.Loading -> CartLoadingStateScreen(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            is CartUiState.Success -> {
                CartSuccesStateScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    state = state,
                    onIncreaseQuantity = { productId, quantity ->
                        cartViewModel.increaseQuantity(
                            productId,
                            quantity
                        )
                    },
                    onDecreaseQuantity = { productId, quantity ->
                        cartViewModel.decreaseQuantity(
                            productId,
                            quantity
                        )
                    },
                    onRemove = { id -> cartViewModel.removeFromCart(id) }
                )
            }

            is CartUiState.Error -> {
                CartErrorStateScreen(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    state
                ) { cartViewModel.loadCart() }
            }
        }
    }
}

@Composable
fun CartErrorStateScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Error,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Error: ${state.message}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onRetry() }) {
            Text("Reintentar")
        }
    }
}

@Composable
fun CartLoadingStateScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
fun CartSuccesStateScreen(
    modifier: Modifier = Modifier,
    state: CartUiState.Success,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    Box(modifier = modifier.padding(16.dp)) {
        AnimatedContent(state.cartItems.isEmpty()) { isEmpty ->
            if (isEmpty) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(54.dp))
                    Text(
                        text = "🛒",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Agrega productos para comenzar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cartItems, key = { it.cartItem.productId }) { itemWithProduct ->
                        CartItemCard(
                            modifier = Modifier.animateItem(),
                            itemWithProduct = itemWithProduct,
                            onIncreaseQuantity = { productId, quantity ->
                                onIncreaseQuantity(
                                    productId,
                                    quantity
                                )
                            },
                            onDecreaseQuantity = { productId, quantity ->
                                onDecreaseQuantity(
                                    productId,
                                    quantity
                                )
                            },
                            onRemove = { id -> onRemove(id) })

                    }
                }
            }
        }

        if (state.cartItems.isNotEmpty() && state.summary != null) {
            CartSummaryCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                summary = state.summary
            )
        }
    }
}

@Composable
fun CartItemCard(
    modifier: Modifier,
    itemWithProduct: CartItemWithPromotion,
    onIncreaseQuantity: (String, Int) -> Unit,
    onDecreaseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val product = itemWithProduct.item.product
    val promotion = itemWithProduct.item.promotion
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when (promotion) {
        is ProductPromotion.BuyXPayY -> product.price
        is ProductPromotion.Percent -> promotion.discountPrice
        null -> product.price
    }

    val hasDiscount = promotion is ProductPromotion.Percent
    val itemTotal = unitPrice * cartItem.quantity

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    Icons.Outlined.Delete, contentDescription = null,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(3f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasDiscount) {
                            Text(
                                text = currencyFormatter.format(product.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Text(
                        text = "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    QuantitySelector(
                        modifier = Modifier.background(
                            color = (MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        oncDecreaseSelected = { onDecreaseQuantity(product.id, cartItem.quantity) },
                        onIncreaseSelected = { onIncreaseQuantity(product.id, cartItem.quantity) }
                    )
                }
            }
        }
    }
}