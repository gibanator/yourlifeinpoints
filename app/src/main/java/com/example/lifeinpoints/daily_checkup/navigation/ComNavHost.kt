package com.example.lifeinpoints.daily_checkup.navigation

// com.example.lifeinpoints.daily_checkup.ui.DailyCheckupNavHost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.daily_checkup.ui.CommentScreen
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupScreen
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupViewModel

@Composable
fun DailyCheckupNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    dailyVm: DailyCheckupViewModel = hiltViewModel(),
    // Передаем необходимые параметры из родительского компонента
) {
    NavHost(
        navController = navController,
        startDestination = ScreenDest.DailyScreen.route,
        modifier = modifier
    ) {
        composable(ScreenDest.DailyScreen.route) {
            DailyCheckupScreen(
                onNavigateToComments = {
                    navController.navigate(ScreenDest.CommentScreen.route)
                },
                vm = dailyVm
            )
        }

        composable(ScreenDest.CommentScreen.route) {
            CommentScreen(
                onBack = {
                    navController.popBackStack()
                         },
                vm = dailyVm,

            )
        }
    }
}