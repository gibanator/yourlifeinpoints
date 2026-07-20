// com/example/lifeinpoints/core/MainActivity.kt
package com.example.lifeinpoints.core

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.R
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.core.navigation.AppNavHost
import com.example.lifeinpoints.core.ui.AppBottomBar
import com.example.lifeinpoints.core.ui.theme.LifeInPointsTheme
import com.example.lifeinpoints.notifications.NotificationHelper
import com.example.lifeinpoints.notifications.NotificationViewModel
import com.example.lifeinpoints.onboarding.OnboardingScreen
import com.example.lifeinpoints.onboarding.OnboardingViewModel
import com.example.lifeinpoints.onboarding.ThemeSelectionScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            AppEntryPoint()  // Точка входа с онбордингом
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
fun AppEntryPoint() {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isLoading by onboardingViewModel.isLoading.collectAsState()
    val onboardingCompleted by onboardingViewModel.onboardingCompleted.collectAsState()
    val themeSelected by onboardingViewModel.themeSelected.collectAsState()

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        when {
            !onboardingCompleted -> {
                OnboardingScreen(
                    onComplete = { onboardingViewModel.completeOnboarding() }
                )
            }
            !themeSelected -> {
                ThemeSelectionScreen(
                    onThemeSelected = { theme ->
                        onboardingViewModel.setThemeSelected(theme)
                    }
                )
            }
            else -> {
                AppWithTopAndBottomBar()
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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Проверяем и планируем уведомления при запуске приложения
    LaunchedEffect(Unit) {
        delay(1000)
        notificationVm.checkNotificationStatus()
    }

    LifeInPointsTheme(themeType = currentTheme) {
        Scaffold(
            bottomBar = {
                AppBottomBar(navController = navController)
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                settingsVm = settingsVm,
                onShowSnackbar = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }
    }
}