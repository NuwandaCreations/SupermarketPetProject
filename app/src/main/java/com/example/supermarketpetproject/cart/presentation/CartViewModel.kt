package com.example.supermarketpetproject.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supermarketpetproject.cart.domain.repository.CartItemRepository
import com.example.supermarketpetproject.cart.domain.usecases.GetCartSummaryUseCase
import com.example.supermarketpetproject.cart.domain.usecases.UpdateCartItemUseCase
import com.example.supermarketpetproject.cart.presentation.model.CartItemWithPromotion
import com.example.supermarketpetproject.productlist.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val getCartSummaryUseCase: GetCartSummaryUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CartEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<CartEvent> = _events

    var cartJob: Job? = null

    init {
        loadCart()
    }

    fun loadCart() {
        _uiState.value = CartUiState.Loading
        cartJob?.cancel()

        cartJob = cartItemRepository.getCartItems()
            .flatMapLatest { cartItems ->
                val ids = cartItems.mapTo(mutableSetOf()) { it.productId }
                if (ids.isEmpty()) {
                    getCartSummaryUseCase().map { summary ->
                        _uiState.value = CartUiState.Success(
                            summary = summary,
                            cartItems = emptyList(),
                            isLoading = false
                        )
                    }
                } else {
                    combine(
                        getCartSummaryUseCase(),
                        productRepository.getProductsByIds(ids)
                    ) { summary, products ->
                        val productsById = products.associateBy { it.id }
                        val cartItemsWithProducts = cartItems.mapNotNull { cartItem ->
                            val finalProduct =
                                productsById[cartItem.productId] ?: return@mapNotNull null
                            CartItemWithPromotion(cartItem = cartItem, product = finalProduct)
                        }
                        _uiState.value = CartUiState.Success(
                            summary = summary,
                            cartItems = cartItemsWithProducts,
                            isLoading = false
                        )
                    }
                }
            }.catch { e ->
                _uiState.value = CartUiState.Error(e.message.orEmpty())
            }.launchIn(viewModelScope)
    }

    fun updateCartItem(productId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                updateCartItem(productId, quantity)
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                cartItemRepository.removeFromCart(productId)
            } catch (e: Exception) {
                _events.emit(CartEvent.ShowMessage(e.message.orEmpty()))
            }
        }
    }

    fun increaseQuantity(productId: String, currentQuantity: Int) {
        updateCartItem(productId, currentQuantity + 1)
    }

    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if (currentQuantity > 1) {
            updateCartItem(productId, currentQuantity - 1)
        } else {
            removeFromCart(productId)
        }
    }
}