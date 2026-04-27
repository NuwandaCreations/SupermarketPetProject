package com.example.supermarketpetproject.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supermarketpetproject.productlist.domain.model.ProductPromotion
import com.example.supermarketpetproject.productlist.domain.model.ProductWithPromotion
import com.example.supermarketpetproject.productlist.domain.model.SortOption
import com.example.supermarketpetproject.productlist.domain.repositories.SettingsRepository
import com.example.supermarketpetproject.productlist.domain.usecases.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    val filterVisible: StateFlow<Boolean> = settingsRepository.filtersVisible.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )


    private var productsJob: Job? = null

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        productsJob?.cancel()
        productsJob = combine(
            getProductsUseCase(),
            settingsRepository.selectedCategory,
            settingsRepository.sortOption
        ) { products, selectedCategory, sortOption ->
            var filteredProducts = products
            if (selectedCategory != null) {
                filteredProducts =
                    filteredProducts.filter { it.product.category == selectedCategory }
            }

            val sortedProducts = when (sortOption) {
                SortOption.PRICE_ASC -> filteredProducts.sortedBy { effectivePrice(it) }
                SortOption.PRICE_DESC -> filteredProducts.sortedByDescending { effectivePrice(it) }
                SortOption.DISCOUNT -> filteredProducts.sortedWith(compareByDescending<ProductWithPromotion> {
                    effectiveDiscountPercent(it)
                }.thenBy { it.promotion == null })

                SortOption.NONE -> filteredProducts
            }

            val categories = products.map { it.product.category }.distinct().sorted()
            ProductListUiState.Success(
                products = sortedProducts,
                categories = categories,
                selectedCategory = selectedCategory,
                sortOption = sortOption
            )
        }.onEach { state ->
            _uiState.value = state
        }.catch { error ->
            _uiState.value = ProductListUiState.Error(error.message.orEmpty())
        }.launchIn(viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            settingsRepository.setSelectedCategory(category)
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            settingsRepository.setSortOption(sortOption)
        }

    }

    fun setFiltersVisible(showFilters: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFiltersVisible(showFilters)
        }
    }

    private fun effectiveDiscountPercent(item: ProductWithPromotion): Double {
        return when (item.promotion) {
            is ProductPromotion.Percent -> item.promotion.percent
            else -> 0.0
        }
    }

    private fun effectivePrice(item: ProductWithPromotion): Double {
        return when (item.promotion) {
            is ProductPromotion.Percent -> item.promotion.discountPrice
            else -> item.product.price
        }
    }
}