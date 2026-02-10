// com/example/lifeinpoints/data/notifications/NotificationPreferences.kt
package com.example.lifeinpoints.data.notifications

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(name = "notification_preferences")

class NotificationPreferences(private val context: Context) {

    companion object {
        private val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")

        // Значения по умолчанию
        private const val DEFAULT_ENABLED = true
        private const val DEFAULT_HOUR = 20 // 20:00 (8 PM)
        private const val DEFAULT_MINUTE = 0
    }

    /**
     * Получает состояние уведомлений (включены/выключены)
     */
    val isNotificationEnabled: Flow<Boolean> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_ENABLED] ?: DEFAULT_ENABLED
        }

    /**
     * Получает час уведомления
     */
    val notificationHour: Flow<Int> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_HOUR] ?: DEFAULT_HOUR
        }

    /**
     * Получает минуту уведомления
     */
    val notificationMinute: Flow<Int> = context.notificationDataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_MINUTE] ?: DEFAULT_MINUTE
        }

    /**
     * Сохраняет состояние уведомлений
     */
    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled
        }
    }

    /**
     * Сохраняет время уведомления
     */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.notificationDataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }
}