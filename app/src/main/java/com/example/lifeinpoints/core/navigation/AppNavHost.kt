package com.example.lifeinpoints.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.MAIN -> /* CategoriesList(CategoryRepository.categories,
                        onCategoryClick = { category -> },) */ DailyCheckupScreen()
                    Destination.CALENDAR -> CalendarScreen()
                    Destination.GRAPHS -> TODO()
                }
            }
        }
    }
}