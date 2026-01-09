package com.example.lifeinpoints.data.dailyCategoryProgress

import com.example.lifeinpoints.data.category.CategoryDao
import kotlinx.coroutines.flow.Flow
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first

class DailyCategoryProgressRepository @Inject constructor(
    private val progressDao: DailyCategoryProgressDao,
    private val categoryDao: CategoryDao
) {
    fun observeAll(): Flow<List<DailyCategoryProgressEntity>> = progressDao.observeAll()

    fun observeByCategoryId(categoryId: Int): Flow<List<DailyCategoryProgressEntity>> =
        progressDao.observeByCategoryId(categoryId)

    suspend fun add(progress: DailyCategoryProgressEntity) = progressDao.insert(progress)

    suspend fun update(progress: DailyCategoryProgressEntity) = progressDao.update(progress)

    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity> = progressDao.getByDate(date)

    suspend fun getByCategoryAndDate(categoryId: Int, date: String): DailyCategoryProgressEntity? =
        progressDao.getByCategoryAndDate(categoryId, date)

    /**
     * Writes the day progress (the done and undone categories, by names) to the database.
     *
     * @param date Date
     * @param completedNames Names of completed categories
     * @param incompletedNames Names of incompleted categories
     */
    suspend fun writeDayByCategoryNames(date: String, completedNames: List<String>, incompletedNames: List<String>){
        val completedIds = categoryDao.getIdsByName(completedNames)
        val incompletedIds = categoryDao.getIdsByName(incompletedNames)

        progressDao.writeDay(date, completedIds, incompletedIds)
    }

    /**
     * Writes the day progress (the done and undone categories, by id) to the database.
     *
     * @param date Date
     * @param completedIds Ids of completed categories
     * @param incompletedIds Ids of incompleted categories
     */
    suspend fun rewriteDayByCategoryIds(date: String, completedIds: List<Int>, incompletedIds: List<Int>) {
        progressDao.rewriteDay(date, completedIds, incompletedIds)
    }

    suspend fun updateComment(categoryId: Int, date: String, comment: String?) =
        progressDao.updateComment(
            categoryId, date, comment
        )

    suspend fun getAll(): List<DailyCategoryProgressEntity> = progressDao.observeAll().first()
}