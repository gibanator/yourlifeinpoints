package com.example.lifeinpoints.ui.navigation

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
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.MAIN -> MainScreen()
                    Destination.CALENDAR -> CalendarScreen()
                    Destination.GRAPHS -> TODO()
                }
            }
        }
    }
}