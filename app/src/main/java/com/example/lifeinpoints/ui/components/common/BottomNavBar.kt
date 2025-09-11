package com.example.lifeinpoints.ui.components.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeinpoints.ui.screens.CalendarScreen
import com.example.lifeinpoints.ui.screens.MainScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: BottomNavItem,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        BottomNavItem.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    BottomNavItem.MAIN -> MainScreen()
                    BottomNavItem.CALENDAR -> CalendarScreen()
                    BottomNavItem.GRAPHS -> TODO()
                }
            }
        }
    }
}

@Composable
fun AppBottomBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
