// com/example/lifeinpoints/notifications/DailyNotificationWorker.kt (обновляем)
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
        const val KEY_DATE = "notification_date"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Получаем данные для уведомления
                val title = inputData.getString(KEY_TITLE)
                    ?: "Daily Checkup Time! ⏰"
                val message = inputData.getString(KEY_MESSAGE)
                    ?: "Complete your daily categories to track your progress. Every day counts! 💪"
                val date = inputData.getString(KEY_DATE)
                    ?: java.time.LocalDate.now().toString()

                // Показываем уведомление
                val notificationHelper = NotificationHelper(applicationContext)
                notificationHelper.showDailyCheckupNotification(title, message)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.retry()
            }
        }
    }
}