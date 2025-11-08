package com.example.lifeinpoints.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                categoriesRepository.observeCategories().collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load categories"
                    )
                }
            }
        }
    }

    suspend fun addCategory(name: String): Result<Unit> {
        return categoriesRepository.addCategory(name)
    }

    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        return categoriesRepository.updateCategory(categoryId, newName)
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return categoriesRepository.deleteCategory(categoryId)
    }

    fun getCategoryById(categoryId: Int): CategoryUiItem? {
        val currentState = _uiState.value
        return currentState.categories.find { it.id == categoryId }
    }

    fun isStaticCategory(categoryId: Int): Boolean {
        return categoriesRepository.isStaticCategory(categoryId)
    }
}