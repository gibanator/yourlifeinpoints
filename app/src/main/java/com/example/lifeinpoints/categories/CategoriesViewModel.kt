// com.example.lifeinpoints.categories/CategoriesViewModel.kt
package com.example.lifeinpoints.categories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.R
import com.example.lifeinpoints.data.category.CategoryEntity
import com.example.lifeinpoints.data.category.CategoryRepositoryNew
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// com.example.lifeinpoints.categories/CategoriesViewModel.kt
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositoryNew,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.observeAll()
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories.map { category -> category.toUiItem() },
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    suspend fun addCategory(name: String, createdAt: Long = System.currentTimeMillis()) {
        categoryRepository.addCategory(name, createdAt)
    }

    suspend fun updateCategory(categoryId: Int, newName: String) {
        val category = categoryRepository.getById(categoryId)
            ?: throw Exception(context.getString(R.string.error_category_not_found))

        categoryRepository.updateCategory(
            categoryId = categoryId,
            name = newName,
            active = category.isActive,
            visible = category.isVisible
        )
    }

    suspend fun deleteCategory(categoryId: Int) {
        try {
            categoryRepository.deleteCategory(categoryId)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun setCategoryVisibility(categoryId: Int, visible: Boolean) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(switchingCategoryIds = it.switchingCategoryIds + categoryId)
                }

                categoryRepository.setCategoryVisibility(categoryId, visible)

                _uiState.update {
                    it.copy(switchingCategoryIds = it.switchingCategoryIds - categoryId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message,
                        switchingCategoryIds = it.switchingCategoryIds - categoryId
                    )
                }
            }
        }
    }

    fun isStaticCategory(categoryId: Int): Boolean {
        val category = _uiState.value.categories.find { it.id == categoryId }
        return category?.isStatic ?: false
    }

    private fun CategoryEntity.toUiItem(): CategoryUiItem {
        return CategoryUiItem(
            id = localId,
            name = name,
            nameKey = null,
            isStatic = isSystem,
            isVisible = isVisible
        )
    }
}
