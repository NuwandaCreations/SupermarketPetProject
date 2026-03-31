package com.example.supermarketpetproject.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.supermarketpetproject.productlist.domain.model.SortOption
import com.example.supermarketpetproject.productlist.domain.usecases.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        getProductsUseCase()
            .onEach { products ->
                val categories = products.map { it.category }.distinct().sorted()
                _uiState.value = ProductListUiState.Success(
                    products = products,
                    categories = categories,
                    selectedCategory = null,
                    sortOption = SortOption.NONE
                )
            }
            .catch { error ->
                _uiState.value = ProductListUiState.Error(error.message.orEmpty())
            }
            .launchIn(viewModelScope)
    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            //Llamar settingRepository
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            //Llamar settingRepository
        }

    }

}