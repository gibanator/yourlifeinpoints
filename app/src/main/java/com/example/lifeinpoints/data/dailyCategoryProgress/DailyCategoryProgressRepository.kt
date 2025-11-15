package com.example.lifeinpoints.data.dailyCategoryProgress

import kotlinx.coroutines.flow.Flow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class DailyCategoryProgressRepository @Inject constructor(
    private val dao: DailyCategoryProgressDao
) {
    fun observeAll(): Flow<List<DailyCategoryProgressEntity>> = dao.observeAll()

    fun observeByCategoryId(categoryId: Int): Flow<List<DailyCategoryProgressEntity>> =
        dao.observeByCategoryId(categoryId)

    suspend fun add(progress: DailyCategoryProgressEntity) = dao.insert(progress)

    suspend fun update(progress: DailyCategoryProgressEntity) = dao.update(progress)

    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity> = dao.getByDate(date)

    suspend fun getByCategoryAndDate(categoryId: Int, date: String): DailyCategoryProgressEntity? =
        dao.getByCategoryAndDate(categoryId, date)

    suspend fun getAll(): List<DailyCategoryProgressEntity> = dao.observeAll().first()
}