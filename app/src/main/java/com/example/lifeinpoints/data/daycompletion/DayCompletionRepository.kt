// com.example.lifeinpoints.data.daycompletion/DayCompletionRepository.kt
package com.example.lifeinpoints.data.daycompletion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayCompletionRepository @Inject constructor(
    private val dao: DayCompletionDao
) {

    suspend fun setDayCompletion(date: String, isCompleted: Boolean) {
        val entity = DayCompletionEntity(date = date, isCompleted = isCompleted)
        dao.insert(entity)
    }

    suspend fun getDayCompletion(date: String): Boolean {
        return dao.getByDate(date)?.isCompleted ?: false
    }

    fun observeDayCompletion(date: String): Flow<Boolean> {
        return dao.observeByDate(date).map { entity ->
            entity?.isCompleted ?: false
        }
    }

    suspend fun getCompletedDaysInRange(startDate: String, endDate: String): List<String> {
        return dao.getByDateRange(startDate, endDate)
            .filter { it.isCompleted }
            .map { it.date }
    }

    suspend fun toggleDayCompletion(date: String): Boolean {
        val current = getDayCompletion(date)
        val newState = !current
        setDayCompletion(date, newState)
        return newState
    }

    // Добавляем метод для подписки на все изменения
    fun observeAllChanges(): Flow<List<DayCompletionEntity>> {
        return dao.observeAllCompletedDays()
    }
}