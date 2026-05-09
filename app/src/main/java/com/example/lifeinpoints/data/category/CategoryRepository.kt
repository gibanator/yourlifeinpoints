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
            id=1,
            name="",
            nameKey="networking",
            color="#FF6B6B",
            sortOrder=0,
            isSystem=true,
            isVisible=true,
            createdAt=0L
        ),
        CategoryEntity(
            id=2,
            name="",
            nameKey="education",
            color="#4ECDC4",
            sortOrder=1,
            isSystem=true,
            isVisible=true,
            createdAt=0L
        ),
        CategoryEntity(
            id=3,
            name="",
            nameKey="work",
            color="#45B7D1",
            sortOrder=2,
            isSystem=true,
            isVisible=true,
            createdAt=0L),
        CategoryEntity(
            id=4,
            name="",
            nameKey="health",
            color="#96CEB4",
            sortOrder=3,
            isSystem=true,
            isVisible=true,
            createdAt=0L
        ),
        CategoryEntity(
            id=5,
            name="",
            nameKey="personal_life",
            color="#FFEAA7",
            sortOrder=4,
            isSystem=true,
            isVisible=true,
            createdAt=0L
        ),
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
    suspend fun addCategory(
        name: String,
        createdAt: Long
    ): Result<Unit> {
        return try {
            val existingCount = dao.countByName(name)
            if (existingCount > 0) {
                Result.failure(Exception("Category '$name' already exists"))
            } else {
                val allCategories = dao.getAll()


                val userCategories = allCategories.filter { !it.isSystem }
                val maxUserSortOrder = userCategories.maxByOrNull { it.sortOrder }?.sortOrder ?: 4


                val newCategory = CategoryEntity(
                    name = name.trim(),
                    color = getDefaultColor(allCategories.size),
                    sortOrder = maxUserSortOrder + 1,
                    isSystem = false,
                    createdAt = createdAt
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
                // Для системных категорий разрешаем только изменение видимости
                // Проверяем, не пытаемся ли изменить имя системной категории
                val existingCategory = dao.getById(category.id)
                if (existingCategory != null && existingCategory.name != category.name) {
                    Result.failure(Exception("Cannot edit name of system category"))
                } else {
                    dao.update(category)
                    Result.success(Unit)
                }
            } else {
                // Для пользовательских категорий проверяем дубликаты имени
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

    /*
    // Проверяем, является ли категория системной
    suspend fun isSystemCategory(categoryId: Int): Boolean {
        return dao.getById(categoryId)?.isSystem ?: false
    }
     */

    // Остальные методы остаются без изменений
    fun observeAll(): Flow<List<CategoryEntity>> {
        return dao.observeAll().map { sortCategories(it) }
    }

    // Обновим метод getAll для правильной сортировки
    suspend fun getAll(): List<CategoryEntity> = sortCategories(dao.getAll())

    private fun sortCategories(categories: List<CategoryEntity>): List<CategoryEntity> {
        val userCategories = categories.filter { !it.isSystem }.sortedByDescending { it.sortOrder }
        val systemCategories = categories.filter { it.isSystem }.sortedBy { it.sortOrder }
        return userCategories + systemCategories
    }

    private fun getDefaultColor(index: Int): String {
        val colors = listOf("#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9")
        return colors[index % colors.size]
    }

    /*
    suspend fun setCategoryVisibility(categoryId: Int, isVisible: Boolean): Result<Unit> {
        return try {
            dao.setVisibility(categoryId, isVisible)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
     */

    // Убедимся, что observeVisibleCategories() возвращает только видимые
    fun observeVisibleCategories(): Flow<List<CategoryEntity>> {
        return dao.observeVisibleCategories().map { sortCategories(it) }
    }

    //suspend fun getVisibleCategories(): List<CategoryEntity> = dao.getVisibleCategories()

    suspend fun getVisibleCategoriesCreatedBefore(fromTime: Long) = dao.getVisibleCategoriesCreatedBefore(fromTime)

    // Обновим системные категории - по умолчанию все видимые
    //suspend fun getAllIds() = dao.getAllIds()

}
