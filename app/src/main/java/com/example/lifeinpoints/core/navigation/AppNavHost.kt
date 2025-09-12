package com.example.lifeinpoints.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.core.ui.TopBarController
import com.example.lifeinpoints.daily_checkup.data.CategoryRepository
import com.example.lifeinpoints.daily_checkup.ui.CategoriesList

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    topBar: TopBarController,
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
                    Destination.MAIN -> CategoriesList(CategoryRepository.categories, topBar = topBar,
                        onCategoryClick = { category -> },)
                    Destination.CALENDAR -> CalendarScreen(topBar = topBar)
                    Destination.GRAPHS -> TODO()
                }
            }
        }
    }
}