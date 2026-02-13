// com.example.lifeinpoints.categories/CategoriesUiState.kt
package com.example.lifeinpoints.categories

data class CategoriesUiState(
    val categories: List<CategoryUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CategoryUiItem(
    val id: Int,
    var name: String,
    var nameKey: String?,
    val isStatic: Boolean = false,
    val isVisible: Boolean = true // Добавляем поле видимости
)