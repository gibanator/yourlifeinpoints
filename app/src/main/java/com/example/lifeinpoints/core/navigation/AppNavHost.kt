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
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.categories.AddCategoryScreen
import com.example.lifeinpoints.categories.CategoriesScreen
import com.example.lifeinpoints.categories.CategoryVisibilityScreen
import com.example.lifeinpoints.categories.EditCategoryScreen
import com.example.lifeinpoints.daily_checkup.navigation.DailyCheckupNavHost
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupViewModel
import com.example.lifeinpoints.statistics.StatisticsScreen  // Импортируем новый экран
import com.example.lifeinpoints.statistics.StatisticsViewModel
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    settingsVm: SettingsViewModel,
    modifier: Modifier = Modifier
) {

    val statisticsViewModel: StatisticsViewModel = hiltViewModel()

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
                if (date != null) vm.onDaySelected(date)
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
                },
                onEditCategory = { categoryId ->
                    navController.navigate("edit_category/$categoryId")
                }
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onCategoriesClick = {
                    navController.navigate("categories")
                },
                onVisibilityClick = {
                    navController.navigate("category_visibility")
                },
                vm = settingsVm
            )
        }

        composable(Routes.Statistics) {
            StatisticsScreen()
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

        // AppNavHost.kt - добавим новый маршрут
        composable("edit_category/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
            EditCategoryScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onCategoryUpdated = {
                    navController.popBackStack()
                    // Можно показать snackbar сообщение об успехе
                },
                onCategoryDeleted = {
                    navController.popBackStack()
                    // Можно показать snackbar сообщение об успехе
                }
            )
        }

        composable("categories") {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onAddCategory = {
                    navController.navigate("add_category")
                },
                onEditCategory = { categoryId ->
                    navController.navigate("edit_category/$categoryId")
                },
                onManageVisibility = { // Добавим новый параметр
                    navController.navigate("category_visibility")
                }
            )
        }

// Добавим новый маршрут для управления видимостью
        composable("category_visibility") {
            CategoryVisibilityScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("category_visibility") {
            CategoryVisibilityScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}