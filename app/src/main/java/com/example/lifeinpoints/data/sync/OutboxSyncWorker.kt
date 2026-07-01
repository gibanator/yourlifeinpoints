package com.example.lifeinpoints.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OutboxSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncer: OutboxSyncer,
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            syncer.syncOnce()
            Result.success()
        } catch (e: Exception) {
            Log.e("OutboxWorker", "Sync failed", e)
            Result.retry()
        }
    }
}