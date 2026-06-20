package com.example.lifeinpoints.data.dailyCategoryProgress

import android.content.Context
import com.example.lifeinpoints.R
import com.example.lifeinpoints.data.remote.api.ProgressApi
import com.example.lifeinpoints.data.remote.progress.ProgressDayRequest
import com.example.lifeinpoints.data.remote.progress.ProgressItemDto
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DailyCategoryProgressRepository @Inject constructor(
    private val progressApi: ProgressApi,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    fun observeAll(): Flow<List<DailyCategoryProgressEntity>> = flowOf(emptyList())

    suspend fun add(progress: DailyCategoryProgressEntity) {
        val current = getByDate(progress.date)
        saveRows(progress.date, upsertRow(current, progress))
    }

    suspend fun update(progress: DailyCategoryProgressEntity) {
        add(progress)
    }

    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity> {
        val authorization = authHeaderOrNull() ?: return emptyList()
        val response = progressApi.getProgress(authorization, date)
        if (response.code() == 401 || response.code() == 404) return emptyList()
        if (!response.isSuccessful) {
            throw IOException(context.getString(R.string.error_progress_load_failed_http, response.code()))
        }

        val items = parseProgressItems(response.body())
        return items.map { item ->
            DailyCategoryProgressEntity(
                categoryId = item.categoryId,
                date = date,
                value = item.completed,
                comment = item.comment
            )
        }
    }

    suspend fun rewriteDayByCategoryIds(
        date: String,
        completedIds: List<Int>,
        incompletedIds: List<Int>
    ) {
        val commentsByCategory = runCatching {
            getByDate(date).associate { it.categoryId to it.comment }
        }.getOrDefault(emptyMap())

        val rows = buildList {
            completedIds.forEach { categoryId ->
                add(
                    DailyCategoryProgressEntity(
                        date = date,
                        categoryId = categoryId,
                        value = true,
                        comment = commentsByCategory[categoryId]
                    )
                )
            }
            incompletedIds.forEach { categoryId ->
                add(
                    DailyCategoryProgressEntity(
                        date = date,
                        categoryId = categoryId,
                        value = false,
                        comment = commentsByCategory[categoryId]
                    )
                )
            }
        }

        saveRows(date, rows)
    }

    suspend fun updateComment(categoryId: Int, date: String, comment: String?) {
        val current = getByDate(date)
        val rows = current.map { row ->
            if (row.categoryId == categoryId) {
                row.copy(comment = comment)
            } else {
                row
            }
        }
        saveRows(date, rows)
    }

    private suspend fun saveRows(date: String, rows: List<DailyCategoryProgressEntity>) {
        val response = progressApi.saveProgress(
            authorization = authHeader(),
            request = ProgressDayRequest(
                date = date,
                items = rows.map { row ->
                    ProgressItemDto(
                        categoryId = row.categoryId,
                        completed = row.value,
                        comment = row.comment
                    )
                }
            )
        )
        if (!response.isSuccessful) {
            throw IOException(context.getString(R.string.error_progress_save_failed_http, response.code()))
        }
    }

    private fun upsertRow(
        rows: List<DailyCategoryProgressEntity>,
        progress: DailyCategoryProgressEntity
    ): List<DailyCategoryProgressEntity> {
        val replaced = rows.any { it.categoryId == progress.categoryId }
        val updated = rows.map { row ->
            if (row.categoryId == progress.categoryId) progress else row
        }
        return if (replaced) updated else updated + progress
    }

    private fun parseProgressItems(body: JsonElement?): List<ProgressItemDto> {
        if (body == null || body.isJsonNull) return emptyList()

        return when {
            body.isJsonArray -> body.asJsonArray.map { element ->
                gson.fromJson(element, ProgressItemDto::class.java)
            }
            body.isJsonObject -> {
                val progress = gson.fromJson(body, ProgressDayRequest::class.java)
                progress.items
            }
            else -> emptyList()
        }
    }

    private suspend fun authHeader(): String {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException(context.getString(R.string.error_user_not_signed_in))
        val token = user.getIdToken(false).awaitResult().token
            ?: throw IllegalStateException(context.getString(R.string.error_firebase_token_missing))
        return "Bearer $token"
    }

    private suspend fun authHeaderOrNull(): String? {
        val user = firebaseAuth.currentUser ?: return null
        val token = user.getIdToken(false).awaitResult().token ?: return null
        return "Bearer $token"
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        val exception = task.exception
        when {
            task.isSuccessful -> continuation.resume(task.result)
            exception != null -> continuation.resumeWithException(exception)
            else -> continuation.resumeWithException(IllegalStateException("Firebase task failed"))
        }
    }
}
