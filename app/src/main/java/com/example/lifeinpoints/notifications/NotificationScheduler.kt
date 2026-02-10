// com/example/lifeinpoints/notifications/NotificationScheduler.kt
package com.example.lifeinpoints.notifications

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        private const val TAG = "NotificationScheduler"
        const val WORK_NAME = DailyNotificationWorker.WORK_NAME
    }

    /**
     * Планирует ежедневное уведомление на указанное время
     */
    fun scheduleDailyNotification(hour: Int, minute: Int) {
        // Отменяем предыдущее уведомление (если есть)
        cancelDailyNotification()

        // Создаём Constraints (ограничения) для Worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()

        // Создаём данные для передачи в Worker
        val inputData = Data.Builder()
            .putString(DailyNotificationWorker.KEY_TITLE, "Daily Checkup Time! ⏰")
            .putString(
                DailyNotificationWorker.KEY_MESSAGE,
                "Complete your daily categories to track your progress. Every day counts! 💪"
            )
            .build()

        // Рассчитываем время до первого уведомления
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        var notificationTime = calendar.timeInMillis

        // Если указанное время уже прошло сегодня, планируем на завтра
        if (notificationTime <= currentTimeMillis) {
            notificationTime += TimeUnit.DAYS.toMillis(1)
        }

        val initialDelay = notificationTime - currentTimeMillis

        Log.d(TAG, "Scheduling notification in ${initialDelay / 1000 / 60} minutes")

        // Создаём периодический WorkRequest (повторяется каждые 24 часа)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            24, // repeatInterval
            TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(WORK_NAME)
            .build()

        // Используем уникальную работу, чтобы не создавать дубликатов
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

        Log.d(TAG, "Daily notification scheduled at $hour:$minute")
    }

    /**
     * Отменяет запланированные уведомления
     */
    fun cancelDailyNotification() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
        Log.d(TAG, "Daily notification cancelled")
    }

    /**
     * Получает LiveData с информацией о запланированной работе
     */
    fun getNotificationWorkInfo(): LiveData<List<WorkInfo>> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(WORK_NAME)
    }

    /**
     * Проверяет, запланировано ли уведомление (синхронная версия)
     * Используем ListenableFuture и корутины
     */
    suspend fun isNotificationScheduled(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Используем ListenableFuture и ждём результат
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(WORK_NAME)
                .get() // Блокирующий вызов, но в Dispatchers.IO это нормально

            workInfos.any { workInfo ->
                workInfo.state == WorkInfo.State.ENQUEUED ||
                        workInfo.state == WorkInfo.State.RUNNING
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking notification status", e)
            false
        }
    }
}