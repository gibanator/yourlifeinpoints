package com.example.lifeinpoints.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lifeinpoints.Settings.SettingsScreen
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.categories.AddCategoryScreen
import com.example.lifeinpoints.categories.CategoriesScreen
import com.example.lifeinpoints.daily_checkup.navigation.DailyCheckupNavHost
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupScreen
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupViewModel
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = Routes.DailyCheckup,
        modifier = modifier
    ) {
        composable(
            route = Routes.DailyCheckup,
        ) {
            DailyCheckupNavHost(
            )
        }

        composable(
            route = Routes.DailyCheckupWithArgs,
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date")
            val date = dateStr?.let { LocalDate.parse(it) }
            val vm: DailyCheckupViewModel = hiltViewModel()
            LaunchedEffect(date) {
                if (date != null) vm.initStateForDay(date)
            }
            DailyCheckupNavHost(
            )
        }
        composable(Routes.Calendar) {
            CalendarScreen(
                toCertainDate = { date ->
                    navController.navigate(Routes.checkupForDay(date)) {
                        launchSingleTop = true
                        restoreState = false
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }

        composable("categories") {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onAddCategory = {
                    navController.navigate("add_category")
                }
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onCategoriesClick = {
                    navController.navigate("categories")
                }
            )
        }

        // Добавим новый composable:

        composable(Routes.ADD_CATEGORY) {
            AddCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryAdded = {
                    navController.popBackStack()
                    // Можно показать snackbar сообщение об успехе
                }
            )
        }

        composable("add_category") {
            AddCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryAdded = {
                    navController.popBackStack()
                    // Можно добавить snackbar сообщение об успехе
                }
            )
        }

    }
}