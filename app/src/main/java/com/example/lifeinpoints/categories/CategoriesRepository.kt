// com.example.lifeinpoints.categories/CategoriesRepository.kt
package com.example.lifeinpoints.categories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CategoriesRepository @Inject constructor() {

    private val staticCategories = listOf(
        CategoryUiItem(id = 1, name = "Networking"),
        CategoryUiItem(id = 2, name = "Education"),
        CategoryUiItem(id = 3, name = "Work"),
        CategoryUiItem(id = 4, name = "Health"),
        CategoryUiItem(id = 5, name = "Personal Life")
    )

    private val dynamicCategories = mutableListOf<CategoryUiItem>()

    fun observeCategories(): Flow<List<CategoryUiItem>> {
        val allCategories = staticCategories + dynamicCategories
        return flowOf(allCategories)
    }

    suspend fun addCategory(name: String): Result<Unit> {
        return try {
            // Проверяем, нет ли уже категории с таким именем
            val allCategories = staticCategories + dynamicCategories
            if (allCategories.any { it.name.equals(name, ignoreCase = true) }) {
                Result.failure(Exception("Category '$name' already exists"))
            } else {
                val newCategory = CategoryUiItem(
                    id = (dynamicCategories.maxByOrNull { it.id }?.id ?: 5) + 1,
                    name = name.trim()
                )
                dynamicCategories.add(newCategory)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CategoriesRepository.kt - добавим новые методы
    suspend fun updateCategory(categoryId: Int, newName: String): Result<Unit> {
        return try {
            // Проверяем, нет ли уже категории с таким именем
            val allCategories = staticCategories + dynamicCategories
            if (allCategories.any { it.id != categoryId && it.name.equals(newName, ignoreCase = true) }) {
                Result.failure(Exception("Category '$newName' already exists"))
            } else {
                // Находим и обновляем категорию
                val categoryToUpdate = dynamicCategories.find { it.id == categoryId }
                if (categoryToUpdate != null) {
                    categoryToUpdate.name = newName
                } else {
                    // Если это статическая категория, создаем копию в dynamic
                    val staticCategory = staticCategories.find { it.id == categoryId }
                    if (staticCategory != null) {
                        dynamicCategories.add(staticCategory.copy(name = newName))
                    }
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return try {
            // Удаляем только из dynamic categories (статические нельзя удалять)
            dynamicCategories.removeAll { it.id == categoryId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}