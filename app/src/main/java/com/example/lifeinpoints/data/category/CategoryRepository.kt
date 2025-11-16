// com.example.lifeinpoints.data.category/CategoryRepository.kt
package com.example.lifeinpoints.data.category

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {

    private val defaultCategories = listOf(
        "Networking",
        "Education",
        "Work",
        "Health",
        "Personal life",
        "Finance",
        "Hobbies",
        "Family",
        "Travel",
        "Self-development"
    )

    suspend fun initializeDefaultCategories(userId: Int) {
        println("DEBUG: initializeDefaultCategories for user $userId")
        try {
            // Получаем существующие категории пользователя
            val existingCategories = dao.getAll()
            println("DEBUG: Existing categories count: ${existingCategories.size}")

            // Добавляем только отсутствующие категории
            val categoriesToAdd = defaultCategories.filter { categoryName ->
                !existingCategories.any { it.name.equals(categoryName, ignoreCase = true) }
            }
            println("DEBUG: Categories to add: ${categoriesToAdd.size}")

            if (categoriesToAdd.isNotEmpty()) {
                val entities = categoriesToAdd.mapIndexed { index, name ->
                    CategoryEntity(
                        name = name,
                        //userId = userId,
                        color = getDefaultColor(index),
                        sortOrder = index
                    )
                }
                println("DEBUG: Inserting ${entities.size} categories")
                dao.insertAll(entities)
                println("DEBUG: Categories inserted successfully")
            }
        } catch (e: Exception) {
            println("DEBUG: Error in initializeDefaultCategories: ${e.message}")
            throw e
        }
    }

    // Добавление новой категории
    suspend fun addCategory(userId: Int, name: String, color: String = "#6200EE"): Result<Unit> {
        return try {
            // Проверяем, нет ли уже категории с таким именем
            val existingCount = dao.countByName(name)
            if (existingCount > 0) {
                Result.failure(Exception("Category '$name' already exists"))
            } else {
                // Получаем максимальный sortOrder для установки новой категории в конец
                val userCategories = dao.getAll()
                val maxSortOrder = userCategories.maxByOrNull { it.sortOrder }?.sortOrder ?: -1

                val newCategory = CategoryEntity(
                    name = name.trim(),
                    //userId = userId,
                    color = color,
                    sortOrder = maxSortOrder + 1
                )
                dao.insert(newCategory)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Обновление категории
    suspend fun updateCategory(category: CategoryEntity): Result<Unit> {
        return try {
            dao.update(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Удаление категории
    suspend fun deleteCategory(category: CategoryEntity): Result<Unit> {
        return try {
            dao.delete(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Получение категории по ID
    suspend fun getById(id: Int): CategoryEntity? = dao.getById(id)

    // Вспомогательная функция для цветов по умолчанию
    private fun getDefaultColor(index: Int): String {
        val colors = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
        )
        return colors[index % colors.size]
    }

    fun observeAll() = dao.observeAll()

    suspend fun getAll() = dao.getAll()
    // Проверка существования категории
    suspend fun categoryExists(userId: Int, categoryName: String): Boolean {
        return dao.countByName(categoryName) > 0
    }

    suspend fun getAllIds() = dao.getAllIds()

}

fun defaultCategories() = listOf(
    CategoryEntity(name = "Networking"),
    CategoryEntity(name = "Education"),
    CategoryEntity(name = "Family"),
    CategoryEntity(name = "Work"),
    CategoryEntity(name = "Health"),
    CategoryEntity(name = "Personal life")
)