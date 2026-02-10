// com/example/lifeinpoints/notifications/NotificationViewModel.kt
package com.example.lifeinpoints.notifications

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.lifeinpoints.data.notifications.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    application: Application,
    private val notificationPreferences: NotificationPreferences
) : AndroidViewModel(application) {

    private val _isNotificationEnabled = MutableStateFlow(false)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _notificationHour = MutableStateFlow(20) // 20:00 по умолчанию
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()

    private val _notificationMinute = MutableStateFlow(0) // 20:00 по умолчанию
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    private val notificationScheduler = NotificationScheduler(application.applicationContext)

    init {
        // Загружаем настройки уведомлений
        loadNotificationSettings()
    }

    /**
     * Загружает настройки уведомлений из хранилища
     */
    private fun loadNotificationSettings() {
        viewModelScope.launch {
            // Комбинируем все три потока в один
            combine(
                notificationPreferences.isNotificationEnabled,
                notificationPreferences.notificationHour,
                notificationPreferences.notificationMinute
            ) { enabled, hour, minute ->
                Triple(enabled, hour, minute)
            }.collect { (enabled, hour, minute) ->
                _isNotificationEnabled.value = enabled
                _notificationHour.value = hour
                _notificationMinute.value = minute

                Log.d("NotificationViewModel", "Loaded settings: enabled=$enabled, time=$hour:$minute")

                // Если уведомления включены, планируем их
                if (enabled) {
                    scheduleNotification(hour, minute)
                } else {
                    cancelNotification()
                }
            }
        }
    }

    /**
     * Включает или выключает уведомления
     */
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            notificationPreferences.setNotificationEnabled(enabled)
            _isNotificationEnabled.value = enabled

            if (enabled) {
                // Планируем уведомление на сохранённое время
                scheduleNotification(_notificationHour.value, _notificationMinute.value)
            } else {
                // Отменяем уведомление
                cancelNotification()
            }
        }
    }

    /**
     * Устанавливает время уведомления
     */
    fun setNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationPreferences.setNotificationTime(hour, minute)
            _notificationHour.value = hour
            _notificationMinute.value = minute

            // Если уведомления включены, перепланируем на новое время
            if (_isNotificationEnabled.value) {
                scheduleNotification(hour, minute)
            }
        }
    }

    /**
     * Планирует уведомление на указанное время
     */
    private fun scheduleNotification(hour: Int, minute: Int) {
        try {
            notificationScheduler.scheduleDailyNotification(hour, minute)
            Log.d("NotificationViewModel", "Notification scheduled for $hour:$minute")
        } catch (e: Exception) {
            Log.e("NotificationViewModel", "Failed to schedule notification", e)
        }
    }

    /**
     * Отменяет уведомление
     */
    private fun cancelNotification() {
        try {
            notificationScheduler.cancelDailyNotification()
            Log.d("NotificationViewModel", "Notification cancelled")
        } catch (e: Exception) {
            Log.e("NotificationViewModel", "Failed to cancel notification", e)
        }
    }

    /**
     * Проверяет, запланировано ли уведомление
     */
    suspend fun checkNotificationStatus() {
        val isScheduled = notificationScheduler.isNotificationScheduled()
        Log.d("NotificationViewModel", "Notification scheduled: $isScheduled")
    }

    /**
     * Получает LiveData для наблюдения за статусом работы уведомлений
     */
    fun getNotificationWorkInfo(): LiveData<List<WorkInfo>> {
        return notificationScheduler.getNotificationWorkInfo()
    }
}