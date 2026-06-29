package com.example.lifeinpoints.data.sync

import com.example.lifeinpoints.data.outbox.OutboxDao
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutboxSyncer @Inject constructor(
    private val outboxDao: OutboxDao,
    private val categoryOutboxHandler: CategoryOutboxHandler,
    private val dailyProgressOutboxHandler: DailyProgressOutboxHandler,
    private val dayCompletionOutboxHandler: DayCompletionOutboxHandler,
    private val auth: AuthTokenProvider
) {

    suspend fun syncOnce() {
        if (!auth.isLoggedIn()) {
            return
        }
        while (true) {
            val operation = outboxDao.getOldest() ?: break

            when (operation.entityType) {
                "Category" -> categoryOutboxHandler.sync(operation)
                "DailyProgress" -> dailyProgressOutboxHandler.sync(operation)
                "DayCompletion" -> dayCompletionOutboxHandler.sync(operation)
                else -> throw IllegalArgumentException("Invalid entity type: ${operation.entityType}")
            }

            outboxDao.deleteById(operation.id)
        }
    }


}