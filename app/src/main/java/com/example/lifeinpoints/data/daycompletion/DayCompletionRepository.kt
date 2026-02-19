// com.example.lifeinpoints.data.daycompletion/DayCompletionRepository.kt
package com.example.lifeinpoints.data.daycompletion

import android.util.Log
import com.example.lifeinpoints.calendar.DayInMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayCompletionRepository @Inject constructor(
    private val dao: DayCompletionDao
) {

    suspend fun ensureRow(date: String) {
        dao.insertIfAbsent(DayCompletionEntity(date = date, isCompleted = false))
    }

    suspend fun getEntityOrDefault(date: String): DayCompletionEntity =
        dao.getByDate(date) ?: DayCompletionEntity(date = date, isCompleted = false)

    suspend fun setState(date: String, isCompleted: Boolean, xpEarned: Int) {
        dao.setState(
            date = date,
            isCompleted = isCompleted,
            completedAt = System.currentTimeMillis(),
            xpEarned = xpEarned
        )
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


    // Добавляем метод для подписки на все изменения
    fun observeAllChanges(): Flow<List<DayCompletionEntity>> {
        return dao.observeAllCompletedDays()
    }

    fun observeYear(year: Int): Flow<Map<LocalDate, DayInMonth.CompletionCategory>> {
        val from = "$year-01-01"
        val to = "$year-12-31"

        return dao.observeRange(from, to).map { rows ->
            rows.associate { row ->
                val date = LocalDate.parse(row.date)
                val cat =
                    if (row.isCompleted)
                        DayInMonth.CompletionCategory.COMPLETED
                    else
                        DayInMonth.CompletionCategory.NONE
                date to cat
            }
        }
    }

    fun observeRange(
        from: LocalDate,
        to: LocalDate
    ): Flow<Map<LocalDate, DayInMonth.CompletionCategory>> =
        observeYear(from.year)

}