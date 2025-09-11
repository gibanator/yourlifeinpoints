package com.example.lifeinpoints.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.daily_checkup.ui.CategoriesListPreview


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
                    Destination.MAIN -> CategoriesListPreview()
                    Destination.CALENDAR -> CalendarScreen()
                    Destination.GRAPHS -> TODO()
                }
            }
        }
    }
}