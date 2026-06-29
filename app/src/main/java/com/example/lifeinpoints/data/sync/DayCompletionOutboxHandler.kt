package com.example.lifeinpoints.data.sync

import com.example.lifeinpoints.data.daycompletion.DayCompletionDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.remote.api.DayCompletionApi
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import java.io.IOException
import javax.inject.Inject

class DayCompletionOutboxHandler @Inject constructor(
    private val dayCompletionDao: DayCompletionDao,
    private val dayCompletionApi: DayCompletionApi,
    private val auth: AuthTokenProvider
) {
    suspend fun sync(operation: OutboxOperationEntity) {
        if (operation.type != OutboxOperationType.SAVE_DAY_COMPLETION) {
            throw IllegalArgumentException("Invalid operation type: ${operation.type}")
        }

        val date = operation.requireSyncKey()

        val token = auth.getAuthorizationHeader()

        val completed = dayCompletionDao.exists(date)
        val response = if (completed) {
            dayCompletionApi.markCompleted(
                authorization = token,
                date = date
            )
        } else {
            dayCompletionApi.unmarkCompleted(
                authorization = token,
                date = date
            )
        }

        if (!response.isSuccessful) {
            throw IOException(
                "Sync day completion failed: HTTP ${response.code()} body=${
                    response.errorBody()?.string()
                }"
            )
        }
    }
}