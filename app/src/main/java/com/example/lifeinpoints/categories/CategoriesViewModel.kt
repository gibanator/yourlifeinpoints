// com.example.lifeinpoints.categories/CategoriesViewModel.kt
package com.example.lifeinpoints.categories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.lifeinpoints.data.remote.category.CategoryDto
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
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
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
                val categories = categoryRepository.getAll()
                _uiState.update {
                    it.copy(
                        categories = categories.mapNotNull { category -> category.toUiItemOrNull() },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: context.getString(R.string.error_categories_load_failed)
                    )
                }
            }
        }
    }

    // Остальные методы без изменений...
    suspend fun addCategory(name: String, createdAt: Long): Result<Unit> {
        return categoryRepository.addCategory(name, createdAt)
            .also { result ->
                if (result.isSuccess) {
                    refreshCategories()
                } else {
                    result.exceptionOrNull()?.let { error ->
                        _uiState.update { it.copy(error = error.message) }
                    }
                }
            }
    }

    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
            categoryRepository.updateCategory(
                categoryId = categoryId,
                name = newName,
                active = category.active,
                visible = category.visible
            ).also { result ->
                if (result.isSuccess) {
                    refreshCategories()
                } else {
                    result.exceptionOrNull()?.let { error ->
                        _uiState.update { it.copy(error = error.message) }
                    }
                }
            }
        } else {
            Result.failure(Exception(context.getString(R.string.error_category_not_found)))
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
            categoryRepository.deleteCategory(categoryId)
                .also { result ->
                    if (result.isSuccess) {
                        refreshCategories()
                    } else {
                        result.exceptionOrNull()?.let { error ->
                            _uiState.update { it.copy(error = error.message) }
                        }
                    }
                }
        } else {
            Result.failure(Exception(context.getString(R.string.error_category_not_found)))
        }
    }

    suspend fun switchCategoryVisibility(categoryId: Int): Result<Unit> {
        val category = categoryRepository.getById(categoryId)
        return if (category != null) {
            _uiState.update {
                it.copy(switchingCategoryIds = it.switchingCategoryIds + categoryId)
            }
            val result = categoryRepository.switchCategoryVisibility(categoryId)
            result.fold(
                onSuccess = { visible ->
                    _uiState.update { state ->
                        state.copy(
                            categories = state.categories.map { item ->
                                if (item.id == categoryId) {
                                    item.copy(isVisible = visible)
                                } else {
                                    item
                                }
                            },
                            error = null,
                            switchingCategoryIds = state.switchingCategoryIds - categoryId
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            error = error.message,
                            switchingCategoryIds = state.switchingCategoryIds - categoryId
                        )
                    }
                }
            )
            result.map { }
        } else {
            Result.failure(Exception(context.getString(R.string.error_category_not_found)))
        }
    }

    fun isStaticCategory(categoryId: Int): Boolean {
        val category = _uiState.value.categories.find { it.id == categoryId }
        return category?.isStatic ?: false
    }

    private suspend fun refreshCategories() {
        val categories = categoryRepository.getAll()
        _uiState.update {
            it.copy(
                categories = categories.mapNotNull { category -> category.toUiItemOrNull() },
                isLoading = false,
                error = null
            )
        }
    }

    private fun CategoryDto.toUiItemOrNull(): CategoryUiItem? {
        val categoryId = id ?: return null
        return CategoryUiItem(
            id = categoryId,
            name = name,
            nameKey = null,
            isStatic = false,
            isVisible = visible
        )
    }
}
