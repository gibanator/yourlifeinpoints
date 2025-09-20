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
import com.example.lifeinpoints.calendar.ui.CalendarScreen
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
              onNavigateToMain = { /* Обработка возврата если нужно */ }
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
            DailyCheckupScreen(vm = vm)
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
    }
}