package com.example.lifeinpoints.core

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.calendar.CalendarViewModel
import com.example.lifeinpoints.core.navigation.AppNavHost
import com.example.lifeinpoints.core.ui.AppBottomBar
import com.example.lifeinpoints.core.ui.theme.LifeInPointsTheme
import com.example.lifeinpoints.notifications.NotificationHelper
import com.example.lifeinpoints.notifications.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Используем строковую константу вместо Manifest.permission
    private val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.POST_NOTIFICATIONS
    } else {
        // Для старых версий не требуется разрешение
        null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Обрабатываем результат запроса разрешения
        if (isGranted) {
            // Разрешение дано
            println("Notification permission granted")
        } else {
            // Разрешение не дано
            println("Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Создаём канал уведомлений
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Запрашиваем разрешение на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission?.let { permission ->
                requestPermissionLauncher.launch(permission)
            }
        }

        setContent {
            AppWithTopAndBottomBar()
        }
    }
}

@Composable
fun AppWithTopAndBottomBar() {
    val navController = rememberNavController()
    val calendarVm: CalendarViewModel = hiltViewModel()
    val settingsVm: SettingsViewModel = hiltViewModel()
    val notificationVm: NotificationViewModel = hiltViewModel() // Добавляем ViewModel уведомлений
    val currentTheme by settingsVm.currentTheme.collectAsState()

    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    // Проверяем и планируем уведомления при запуске приложения
    LaunchedEffect(Unit) {
        // Даём небольшую задержку, чтобы приложение успело инициализироваться
        delay(1000)
        notificationVm.checkNotificationStatus()
    }

    LifeInPointsTheme(themeType = currentTheme) {
        Scaffold(
            bottomBar = {
                AppBottomBar(navController = navController)
            }
        ) { paddingValues ->
            val layoutDirection = LocalLayoutDirection.current

            AppNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        end = paddingValues.calculateEndPadding(layoutDirection),
                        bottom = paddingValues.calculateBottomPadding()
                    ),
                settingsVm = settingsVm
            )
        }
    }
}