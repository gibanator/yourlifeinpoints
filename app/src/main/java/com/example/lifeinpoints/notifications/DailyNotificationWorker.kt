// com/example/lifeinpoints/notifications/DailyNotificationWorker.kt (обновляем)
package com.example.lifeinpoints.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
//import androidx.work.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lifeinpoints.R

class DailyNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_checkup_notification_work"
        const val KEY_DATE = "notification_date"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                //val date = inputData.getString(KEY_DATE)
                 //   ?: java.time.LocalDate.now().toString()

                val title = applicationContext.getString(R.string.notif_daily_title)
                val message = applicationContext.getString(R.string.notif_daily_message)

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