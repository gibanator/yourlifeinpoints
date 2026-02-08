// com/example/lifeinpoints/notifications/DailyNotificationWorker.kt
package com.example.lifeinpoints.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_checkup_notification_work"
        const val KEY_TITLE = "notification_title"
        const val KEY_MESSAGE = "notification_message"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Получаем данные для уведомления
                val title = inputData.getString(KEY_TITLE)
                    ?: "Daily Checkup Reminder"
                val message = inputData.getString(KEY_MESSAGE)
                    ?: "Don't forget to complete your daily checkup!"

                // Показываем уведомление
                val notificationHelper = NotificationHelper(applicationContext)
                notificationHelper.showDailyCheckupNotification(title, message)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry() // Пробуем снова при следующем запуске
            }
        }
    }
}