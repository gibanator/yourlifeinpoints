// com/example/lifeinpoints/core/MainActivity.kt
package com.example.lifeinpoints.core

import android.content.Intent
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
import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.core.navigation.AppNavHost
import com.example.lifeinpoints.core.ui.AppBottomBar
import com.example.lifeinpoints.core.ui.theme.LifeInPointsTheme
import com.example.lifeinpoints.notifications.NotificationHelper
import com.example.lifeinpoints.notifications.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var notificationHelper: NotificationHelper

    private val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Notification permission granted")
        } else {
            println("Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Создаём канал уведомлений
        notificationHelper = NotificationHelper(this)
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

        // Обрабатываем нажатие на уведомление
        handleNotificationClick(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Обрабатываем нажатие на уведомление, когда приложение уже запущено
        handleNotificationClick(intent)
    }

    /**
     * Обрабатывает нажатие на уведомление
     */
    private fun handleNotificationClick(intent: Intent?) {
        if (intent != null) {
            val fromNotification = intent.getBooleanExtra("from_notification", false)
            if (fromNotification) {
                val date = intent.getStringExtra("notification_date")
                println("App opened from notification with date: $date")
                // Здесь можно добавить дополнительную логику,
                // например, показать snackbar или перейти на определенный экран
            }

            val action = intent.getStringExtra("action")
            when (action) {
                NotificationHelper.ACTION_COMPLETE_TODAY -> {
                    println("Complete today action clicked")
                    // Здесь можно добавить логику для действия "Выполнить сегодня"
                }
                NotificationHelper.ACTION_POSTPONE -> {
                    println("Postpone action clicked")
                    // Здесь можно добавить логику для действия "Отложить"
                }
            }
        }
    }
}

@Composable
fun AppWithTopAndBottomBar() {
    val navController = rememberNavController()
    val settingsVm: SettingsViewModel = hiltViewModel()
    val notificationVm: NotificationViewModel = hiltViewModel()
    val currentTheme by settingsVm.currentTheme.collectAsState()

    //val context = LocalContext.current
    //val notificationHelper = remember { NotificationHelper(context) }

    // Проверяем и планируем уведомления при запуске приложения
    LaunchedEffect(Unit) {
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