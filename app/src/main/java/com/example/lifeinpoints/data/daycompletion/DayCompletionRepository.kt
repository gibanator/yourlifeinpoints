// com.example.lifeinpoints.data.daycompletion/DayCompletionRepository.kt
package com.example.lifeinpoints.data.daycompletion

//import android.util.Log
import androidx.room.withTransaction
import com.example.lifeinpoints.calendar.DayInMonth
import com.example.lifeinpoints.data.AppDatabase
import com.example.lifeinpoints.data.outbox.OutboxDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DayCompletionRepository @Inject constructor(
    private val database: AppDatabase,
    private val dao: DayCompletionDao,
    private val syncScheduler: SyncScheduler,
    private val outboxDao: OutboxDao
) {
    suspend fun getCompletedEntity(date: String): DayCompletionEntity? {
        return dao.getByDate(date)
    }

    suspend fun markCompleted(date: String, xpEarned: Int) {
        database.withTransaction {
            dao.insert(
                DayCompletionEntity(
                    date = date,
                    xpEarned = xpEarned
                )
            )

            enqueueSaveDayCompletion(date)
        }

        syncScheduler.enqueueSync()
    }

    suspend fun unmarkCompleted(date: String) {
        database.withTransaction {
            dao.deleteByDate(date)

            enqueueSaveDayCompletion(date)
        }

        syncScheduler.enqueueSync()
    }

    suspend fun isCompleted(date: String): Boolean {
        return dao.exists(date)
    }

    fun observeDayCompletion(date: String): Flow<Boolean> {
        return dao.observeByDate(date).map { entity ->
            entity != null
        }
    }

    fun observeAllChanges(): Flow<List<DayCompletionEntity>> {
        return dao.observeAllCompletedDays()
    }

    fun observeYear(year: Int): Flow<Map<LocalDate, DayInMonth.CompletionCategory>> {
        val from = "$year-01-01"
        val to = "$year-12-31"

        return dao.observeRange(from, to).map { rows ->
            rows.associate { row ->
                LocalDate.parse(row.date) to DayInMonth.CompletionCategory.COMPLETED
            }
        }
    }

    private suspend fun enqueueSaveDayCompletion(date: String) {
        val alreadyQueued =
            outboxDao.countByTypeAndKey(
                type = OutboxOperationType.SAVE_DAY_COMPLETION,
                entityType = DAY_COMPLETION_ENTITY_TYPE,
                syncKey = date
            ) > 0

        if (!alreadyQueued) {
            outboxDao.insert(
                OutboxOperationEntity(
                    type = OutboxOperationType.SAVE_DAY_COMPLETION,
                    entityType = DAY_COMPLETION_ENTITY_TYPE,
                    syncKey = date
                )
            )
        }
    }
    private companion object {
        const val DAY_COMPLETION_ENTITY_TYPE = "DayCompletion"
    }
}