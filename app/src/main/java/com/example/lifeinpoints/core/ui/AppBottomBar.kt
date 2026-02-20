package com.example.lifeinpoints.core.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment  // Иконка для статистики
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lifeinpoints.core.navigation.Routes
import com.example.lifeinpoints.R

@Composable
fun AppBottomBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute?.substringBefore("?") == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                label = { Text(stringResource(item.labelRes)) }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @get:StringRes val labelRes: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DAILY_CHECKUP_WITH_ARGS, Icons.Default.Home, R.string.main_page_navbar_item),
    BottomNavItem(Routes.CALENDAR, Icons.Default.DateRange, R.string.calendar_page_navbar_item),
    BottomNavItem(Routes.STATISTICS, Icons.Default.Assessment, R.string.statistics_page_navbar_item),
    BottomNavItem(Routes.SETTINGS, Icons.Default.Settings, R.string.settings_page_navbar_item)
)
