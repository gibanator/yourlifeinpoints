package com.example.lifeinpoints.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lifeinpoints.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "daily_checkup_channel"
        const val CHANNEL_NAME = "Daily Checkup Reminders"
        const val CHANNEL_DESCRIPTION = "Reminds you to complete your daily checkup"
        const val NOTIFICATION_ID = 1

        // Константа для разрешения (если Manifest.permission недоступна)
        const val POST_NOTIFICATIONS_PERMISSION = "android.permission.POST_NOTIFICATIONS"
    }

    /**
     * Создаёт канал уведомлений (обязательно для API 26+)
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    /**
     * Проверяет, есть ли разрешение на показ уведомлений
     */
    fun canShowNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ проверяем разрешение
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            // Для старых версий уведомления включены по умолчанию
            true
        }
    }

    /**
     * Показывает уведомление о ежедневном чекапе
     * @SuppressLint используется для игнорирования проверки разрешения в Lint
     * Мы проверяем разрешение в canShowNotifications()
     */
    @SuppressLint("MissingPermission")
    fun showDailyCheckupNotification(title: String, message: String) {
        // Проверяем, можем ли показывать уведомления
        if (!canShowNotifications()) {
            return
        }

        try {
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: Exception) {
            // Логируем ошибку, но не падаем
            e.printStackTrace()
        }
    }

    /**
     * Отменяет уведомление
     */
    fun cancelNotification() {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}