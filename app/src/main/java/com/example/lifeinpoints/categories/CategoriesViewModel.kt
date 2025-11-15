// com.example.lifeinpoints.categories/CategoriesViewModel.kt
package com.example.lifeinpoints.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.lifeinpoints.data.category.CategoryRepository
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository // Используем репозиторий из data.category
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    // com.example.lifeinpoints.categories/CategoriesViewModel.kt
    fun loadCategories() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                categoryRepository.initializeSystemCategories()

                categoryRepository.observeAll().collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories.map { category ->
                                CategoryUiItem(
                                    id = category.id,
                                    name = category.name,
                                    isStatic = category.isSystem
                                )
                            },
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
        return categoryRepository.addCategory(name)
    }

    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
            // Проверяем, не системная ли это категория
            if (category.isSystem) {
                Result.failure(Exception("Cannot edit system category"))
            } else {
                categoryRepository.updateCategory(category.copy(name = newName))
            }
        } else {
            Result.failure(Exception("Category not found"))
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
            if (category.isSystem) {
                Result.failure(Exception("Cannot delete system category"))
            } else {
                categoryRepository.deleteCategory(category)
            }
        } else {
            Result.failure(Exception("Category not found"))
        }
    }

    fun getCategoryById(categoryId: Int): CategoryUiItem? {
        val currentState = _uiState.value
        return currentState.categories.find { it.id == categoryId }
    }

    fun isStaticCategory(categoryId: Int): Boolean {
        val category = _uiState.value.categories.find { it.id == categoryId }
        return category?.isStatic ?: false
    }
}