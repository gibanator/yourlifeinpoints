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

// com.example.lifeinpoints.categories/CategoriesViewModel.kt
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
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
                categoryRepository.observeAll().collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories.map { category ->
                                CategoryUiItem(
                                    id = category.id,
                                    name = category.name,
                                    isStatic = category.isSystem,
                                    isVisible = category.isVisible, // Добавляем состояние видимости
                                    nameKey = category.nameKey
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

    // Остальные методы без изменений...
    suspend fun addCategory(name: String, createdAt: Long): Result<Unit> {
        return categoryRepository.addCategory(name, createdAt)
    }

    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
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

    // Метод для изменения видимости - теперь работает для всех категорий
    // Метод для изменения видимости
    suspend fun setCategoryVisibility(categoryId: Int, isVisible: Boolean): Result<Unit> {
        return try {
            val category = categoryRepository.getById(categoryId)
            if (category != null) {
                val updatedCategory = category.copy(isVisible = isVisible)
                categoryRepository.updateCategory(updatedCategory)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isStaticCategory(categoryId: Int): Boolean {
        val category = _uiState.value.categories.find { it.id == categoryId }
        return category?.isStatic ?: false
    }
}