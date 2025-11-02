package com.example.lifeinpoints.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.calendar.CalendarViewModel
import com.example.lifeinpoints.core.navigation.AppNavHost
import com.example.lifeinpoints.core.ui.AppBottomBar
import com.example.lifeinpoints.core.ui.theme.LifeInPointsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppWithTopAndBottomBar()
        }
    }
}

@Composable
fun AppWithTopAndBottomBar() {
    val navController = rememberNavController()
    val calendarVm: CalendarViewModel = hiltViewModel()
    val settingsVm: SettingsViewModel = hiltViewModel() // Добавляем SettingsViewModel

    // Собираем состояние текущей темы
    val currentTheme by settingsVm.currentTheme.collectAsState()

    // Оборачиваем всё в нашу тему с выбранным типом
    LifeInPointsTheme(themeType = currentTheme) {
        Scaffold(
            bottomBar = {
                AppBottomBar(navController = navController)
            }
        ) { padding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = padding.calculateBottomPadding()
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewAppWithTopAndBottomBar() {
    // Для превью используем системную тему или любую другую по умолчанию
    LifeInPointsTheme {
        AppWithTopAndBottomBar()
    }
}