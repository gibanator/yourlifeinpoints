// com.example.lifeinpoints.data.category/CategoryRepository.kt
package com.example.lifeinpoints.data.category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Добавляем аннотацию Singleton
class CategoryRepository @Inject constructor(
    private val dao: CategoryDao
) {

    // Системные категории, которые нельзя удалить/редактировать
    private val systemCategories = listOf(
        CategoryEntity(
            id = 1,
            name = "Networking",
            color = "#FF6B6B",
            sortOrder = 0,
            isSystem = true
        ),
        CategoryEntity(
            id = 2,
            name = "Education",
            color = "#4ECDC4",
            sortOrder = 1,
            isSystem = true
        ),
        CategoryEntity(
            id = 3,
            name = "Work",
            color = "#45B7D1",
            sortOrder = 2,
            isSystem = true
        ),
        CategoryEntity(
            id = 4,
            name = "Health",
            color = "#96CEB4",
            sortOrder = 3,
            isSystem = true
        ),
        CategoryEntity(
            id = 5,
            name = "Personal Life",
            color = "#FFEAA7",
            sortOrder = 4,
            isSystem = true
        )
    )

    // Инициализация системных категорий при первом запуске
    suspend fun initializeSystemCategories() {
        try {
            val existingCategories = dao.getAll()

            // Добавляем системные категории, если их еще нет
            systemCategories.forEach { systemCategory ->
                val exists = existingCategories.any { it.id == systemCategory.id }
                if (!exists) {
                    dao.insert(systemCategory)
                }
            }
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем выполнение
            println("Error initializing system categories: ${e.message}")
        }
    }

    // Остальные методы остаются без изменений...
    // Обновим метод addCategory - новые категории должны получать максимальный sortOrder
    suspend fun addCategory(name: String): Result<Unit> {
        return try {
            val existingCount = dao.countByName(name)
            if (existingCount > 0) {
                Result.failure(Exception("Category '$name' already exists"))
            } else {
                val allCategories = dao.getAll()

                // Находим максимальный sortOrder среди пользовательских категорий
                val userCategories = allCategories.filter { !it.isSystem }
                val maxUserSortOrder = userCategories.maxByOrNull { it.sortOrder }?.sortOrder ?: 4

                val newCategory = CategoryEntity(
                    name = name.trim(),
                    color = getDefaultColor(allCategories.size),
                    sortOrder = maxUserSortOrder + 1, // Новая категория получает sortOrder больше текущего максимума
                    isSystem = false
                )
                dao.insert(newCategory)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // При обновлении проверяем, не системная ли категория
    suspend fun updateCategory(category: CategoryEntity): Result<Unit> {
        return try {
            if (category.isSystem) {
                Result.failure(Exception("Cannot edit system category"))
            } else {
                // Проверяем, нет ли дубликатов имени (исключая текущую категорию)
                val existingCategories = dao.getAll()
                val duplicate = existingCategories.any {
                    it.id != category.id && it.name.equals(category.name, ignoreCase = true)
                }

                if (duplicate) {
                    Result.failure(Exception("Category '${category.name}' already exists"))
                } else {
                    dao.update(category)
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // При удалении проверяем, не системная ли категория
    suspend fun deleteCategory(category: CategoryEntity): Result<Unit> {
        return try {
            if (category.isSystem) {
                Result.failure(Exception("Cannot delete system category"))
            } else {
                dao.delete(category)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Получаем категорию по ID с проверкой на системность
    suspend fun getById(id: Int): CategoryEntity? = dao.getById(id)

    // Проверяем, является ли категория системной
    suspend fun isSystemCategory(categoryId: Int): Boolean {
        return dao.getById(categoryId)?.isSystem ?: false
    }

    // Остальные методы остаются без изменений
    fun observeAll(): Flow<List<CategoryEntity>> {
        return dao.observeAll().map { categories ->
            // Сначала пользовательские категории (новые сверху), затем системные
            val userCategories = categories
                .filter { !it.isSystem }
                .sortedByDescending { it.sortOrder } // Новые пользовательские категории сверху

            val systemCategories = categories
                .filter { it.isSystem }
                .sortedBy { it.sortOrder } // Системные в оригинальном порядке

            userCategories + systemCategories
        }
    }

    // Обновим метод getAll для правильной сортировки
    suspend fun getAll(): List<CategoryEntity> {
        val categories = dao.getAll()
        val userCategories = categories
            .filter { !it.isSystem }
            .sortedByDescending { it.sortOrder }

        val systemCategories = categories
            .filter { it.isSystem }
            .sortedBy { it.sortOrder }

        return userCategories + systemCategories
    }

    private fun getDefaultColor(index: Int): String {
        val colors = listOf("#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9")
        return colors[index % colors.size]
    }
}