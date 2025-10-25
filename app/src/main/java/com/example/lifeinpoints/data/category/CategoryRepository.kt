package com.example.lifeinpoints.data.category

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao,
) {
    // Получение всех категорий (для администрирования или всех пользователей)
    fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()

    suspend fun getAll(): List<CategoryEntity> = dao.observeAll().first()

    // Работа с категориями конкретного пользователя
    fun observeByUserId(userId: Int): Flow<List<CategoryEntity>> = dao.observeByUserId(userId)

    suspend fun getByUserId(userId: Int): List<CategoryEntity> = dao.getByUserId(userId)

    // Добавление категории с указанием пользователя
    suspend fun add(category: CategoryEntity) = dao.insert(category)

    suspend fun addCategory(userId: Int, name: String) {
        val category = CategoryEntity(name = name, userId = userId)
        dao.insert(category)
    }

    // Обновление категории
    suspend fun update(category: CategoryEntity) = dao.update(category)

    // Удаление категории
    suspend fun delete(category: CategoryEntity) = dao.delete(category)

    // Удаление всех категорий пользователя
    suspend fun deleteByUserId(userId: Int) = dao.deleteByUserId(userId)

    // Получение категории по ID
    suspend fun getById(id: Int): CategoryEntity? = dao.getById(id)

    // Дополнительные методы для удобства
    suspend fun addCategories(userId: Int, categoryNames: List<String>) {
        val categories = categoryNames.map { name ->
            CategoryEntity(name = name, userId = userId)
        }
        dao.insertAll(categories)
    }

    // Проверка существования категории у пользователя
    suspend fun categoryExistsForUser(userId: Int, categoryName: String): Boolean {
        val userCategories = getByUserId(userId)
        return userCategories.any { it.name.equals(categoryName, ignoreCase = true) }
    }
}