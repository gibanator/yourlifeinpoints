package com.example.lifeinpoints.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dao: CategoryDao,
) {
    fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()
    suspend fun add(category: CategoryEntity) = dao.insert(category)

    suspend fun getAll(): List<CategoryEntity> = dao.observeAll().first()
}