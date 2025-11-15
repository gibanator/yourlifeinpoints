// com.example.lifeinpoints.categories/CategoriesRepository.kt
package com.example.lifeinpoints.categories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CategoriesRepository @Inject constructor() {

    private val staticCategories = listOf(
        CategoryUiItem(id = 1, name = "Networking", isStatic = true),
        CategoryUiItem(id = 2, name = "Education", isStatic = true),
        CategoryUiItem(id = 3, name = "Work", isStatic = true),
        CategoryUiItem(id = 4, name = "Health", isStatic = true),
        CategoryUiItem(id = 5, name = "Personal Life", isStatic = true)
    )

    private val dynamicCategories = mutableListOf<CategoryUiItem>()

    // Используем StateFlow для мгновенных обновлений
    private val _categories = MutableStateFlow(staticCategories + dynamicCategories)
    val categories: StateFlow<List<CategoryUiItem>> = _categories.asStateFlow()

    fun observeCategories(): Flow<List<CategoryUiItem>> = categories

    private fun updateCategories() {
        // Сначала динамические (пользовательские), потом статические
        _categories.value = dynamicCategories + staticCategories
    }

    suspend fun addCategory(name: String): Result<Unit> {
        return try {
            // Проверяем, нет ли уже категории с таким именем
            val allCategories = dynamicCategories + staticCategories
            if (allCategories.any { it.name.equals(name, ignoreCase = true) }) {
                Result.failure(Exception("Category '$name' already exists"))
            } else {
                val newCategory = CategoryUiItem(
                    id = (dynamicCategories.maxByOrNull { it.id }?.id ?: 100) + 1, // Начинаем с 100+ для пользовательских
                    name = name.trim(),
                    isStatic = false
                )
                // Добавляем в начало списка
                dynamicCategories.add(0, newCategory)
                updateCategories() // Обновляем StateFlow
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        return try {
            // Проверяем, нет ли уже категории с таким именем (исключая текущую)
            val allCategories = dynamicCategories + staticCategories
            if (allCategories.any { it.id != categoryId && it.name.equals(newName, ignoreCase = true) }) {
                Result.failure(Exception("Category '$newName' already exists"))
            } else {
                // Находим категорию для обновления (только динамические можно редактировать)
                val categoryToUpdate = dynamicCategories.find { it.id == categoryId }
                if (categoryToUpdate != null) {
                    categoryToUpdate.name = newName
                    updateCategories() // Обновляем StateFlow
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Cannot edit system category"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            // Удаляем только из dynamic categories (статические нельзя удалять)
            val categoryToDelete = dynamicCategories.find { it.id == categoryId }
            if (categoryToDelete != null) {
                dynamicCategories.remove(categoryToDelete)
                updateCategories() // Обновляем StateFlow
                Result.success(Unit)
            } else {
                Result.failure(Exception("Cannot delete system category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Метод для проверки, является ли категория статической
    fun isStaticCategory(categoryId: Int): Boolean {
        return staticCategories.any { it.id == categoryId }
    }
}