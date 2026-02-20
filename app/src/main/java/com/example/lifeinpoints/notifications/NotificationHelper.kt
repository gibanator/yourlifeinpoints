// com/example/lifeinpoints/notifications/NotificationHelper.kt
package com.example.lifeinpoints.notifications

//import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
//import androidx.core.app.TaskStackBuilder
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.MainActivity
import java.time.LocalDate

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "daily_checkup_channel"
        const val CHANNEL_NAME = "Daily Checkup Reminders"
        const val CHANNEL_DESCRIPTION = "Reminds you to complete your daily checkup"
        const val NOTIFICATION_ID = 1

        // Константы для действий в уведомлении
        const val ACTION_COMPLETE_TODAY = "action_complete_today"
        const val ACTION_POSTPONE = "action_postpone"

        // Константа для разрешения
        //const val POST_NOTIFICATIONS_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }

    /**
     * Создаёт канал уведомлений (обязательно для API 26+)
     */
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500)
            setShowBadge(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Проверяет, есть ли разрешение на показ уведомлений
     */
    fun canShowNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true
        }
    }

    /**
     * Создаёт PendingIntent для открытия приложения при нажатии на уведомление
     */
    private fun createMainActivityIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("from_notification", true)
            putExtra("notification_date", LocalDate.now().toString())
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Создаёт PendingIntent для действия "Выполнить сегодня"
     */
/*
private fun createCompleteTodayIntent(): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = ACTION_COMPLETE_TODAY
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("action", ACTION_COMPLETE_TODAY)
        putExtra("date", LocalDate.now().toString())
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    } else {
        PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

 */

/**
 * Создаёт PendingIntent для действия "Отложить"
 */

/*
private fun createPostponeIntent(): PendingIntent {
val intent = Intent(context, MainActivity::class.java).apply {
    action = ACTION_POSTPONE
    putExtra("action", ACTION_POSTPONE)
}

return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    PendingIntent.getBroadcast(
        context,
        2,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
} else {
    PendingIntent.getBroadcast(
        context,
        2,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}
}
*/

/**
* Показывает уведомление о ежедневном чекапе
*/
@SuppressLint("MissingPermission")
fun showDailyCheckupNotification(title: String, message: String) {
// Проверяем, можем ли показывать уведомления
if (!canShowNotifications()) {
    return
}

try {
    // Создаём основной PendingIntent для нажатия на уведомление
    val contentIntent = createMainActivityIntent()

    // Создаём уведомление с действиями
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_lipicon_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setAutoCancel(true)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setContentIntent(contentIntent)
        .setOngoing(false)
        .setOnlyAlertOnce(true)
        // Добавляем действие "Выполнить сегодня"
        .addAction(
            R.drawable.ic_notification,
            "Open App",
            contentIntent
        )

    NotificationManagerCompat.from(context)
        .notify(NOTIFICATION_ID, notificationBuilder.build())
} catch (e: Exception) {
    e.printStackTrace()
}
}

/**
* Показывает тестовое уведомление
*/

/*
@SuppressLint("MissingPermission")
fun showTestNotification() {
if (!canShowNotifications()) {
    return
}

try {
    val contentIntent = createMainActivityIntent()

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Test Notification 🧪")
        .setContentText("This is a test notification to check if everything is working!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setAutoCancel(true)
        .setContentIntent(contentIntent)
        .addAction(
            R.drawable.ic_notification,
            "Open App",
            contentIntent
        )

    NotificationManagerCompat.from(context)
        .notify(999, notificationBuilder.build()) // Используем другой ID для теста
} catch (e: Exception) {
    e.printStackTrace()
}
}

*/
/**
* Отменяет уведомление
*/

/*
fun cancelNotification() {
try {
    NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
} catch (e: Exception) {
    e.printStackTrace()
}
}
*/
}