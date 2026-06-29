package com.example.lifeinpoints.data.dailyCategoryProgress

import androidx.room.withTransaction
import com.example.lifeinpoints.data.AppDatabase
import com.example.lifeinpoints.data.outbox.OutboxDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyCategoryProgressRepositoryNew @Inject constructor(
    private val database: AppDatabase,
    private val dao: DailyCategoryProgressDao,
    private val syncScheduler: SyncScheduler,
    private val outboxDao: OutboxDao,
) {
    fun observeDay(date: String): Flow<List<DailyCategoryProgressEntity>> =
        dao.observeByDate(date)

    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity> =
        dao.getByDate(date)


    suspend fun saveDay(
        date: String,
        rows: List<DailyCategoryProgressEntity>
    ) {
        database.withTransaction {
            dao.rewriteDay(
                date = date,
                rows = rows
            )
            val alreadyQueued = outboxDao.countByTypeAndKey(
                type = OutboxOperationType.SAVE_DAILY_PROGRESS,
                entityType = DAILY_PROGRESS_ENTITY_TYPE,
                syncKey = date
            ) > 0
            if (!alreadyQueued) {
                outboxDao.insert(
                    OutboxOperationEntity(
                        type = OutboxOperationType.SAVE_DAILY_PROGRESS,
                        entityType = DAILY_PROGRESS_ENTITY_TYPE,
                        entityLocalId = 0,
                        syncKey = date
                    )
                )
            }
        }
        syncScheduler.enqueueSync()
    }

    private companion object {
        const val DAILY_PROGRESS_ENTITY_TYPE = "DailyProgress"
    }
}