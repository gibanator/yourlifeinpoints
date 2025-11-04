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
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    suspend fun addCategory(name: String): Result<Unit> {
        return categoriesRepository.addCategory(name)
    }

    // CategoriesViewModel.kt - добавим новые методы
    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        return try {
            categoriesRepository.updateCategory(categoryId, newName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            categoriesRepository.deleteCategory(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCategoryById(categoryId: Int): CategoryUiItem? {
        val currentState = _uiState.value
        return currentState.categories.find { it.id == categoryId }
    }
}