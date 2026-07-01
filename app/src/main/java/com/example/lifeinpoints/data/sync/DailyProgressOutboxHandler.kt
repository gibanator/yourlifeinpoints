package com.example.lifeinpoints.data.sync

import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.remote.api.ProgressApi
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import com.example.lifeinpoints.data.remote.progress.ProgressDayRequest
import com.example.lifeinpoints.data.remote.progress.ProgressItemDto
import java.io.IOException
import javax.inject.Inject

class DailyProgressOutboxHandler @Inject constructor(
    private val dailyProgressDao: DailyCategoryProgressDao,
    private val categoryDao: CategoryDao,
    private val dailyProgressApi: ProgressApi,
    private val auth: AuthTokenProvider
) {
    suspend fun sync(operation: OutboxOperationEntity) {
        if (operation.type != OutboxOperationType.SAVE_DAILY_PROGRESS) {
            throw IllegalArgumentException("Invalid operation type: ${operation.type}")
        }

        val date = operation.requireSyncKey()
        val dailyProgress = dailyProgressDao.getByDate(date)

        val localCategoryIds = dailyProgress.map { it.categoryLocalId }
        val categories = categoryDao.getByLocalIds(localCategoryIds)

        val serverIdByLocalId = categories.associate {
            it.localId to it.serverId
        }

        val items = dailyProgress.map { row ->
            val serverId = serverIdByLocalId[row.categoryLocalId]
                ?: throw IOException("Category ${row.categoryLocalId} has no serverId")
            ProgressItemDto(
                categoryId = serverId.toInt(),
                completed = row.value,
                comment = row.comment
            )
        }

        val token = auth.getAuthorizationHeader()

        val response = dailyProgressApi.saveProgress(
            authorization = token,
            request = ProgressDayRequest(
                date = date,
                items = items
            )
        )

        if (!response.isSuccessful) {
            throw IOException("Save daily progress failed: HTTP ${response.code()} body=${response.errorBody()?.string()}")
        }
    }
}