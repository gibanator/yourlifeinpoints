package com.example.lifeinpoints.daily_checkup.navigation

// com.example.lifeinpoints.daily_checkup.ui.DailyCheckupNavHost

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.daily_checkup.ui.CommentScreen
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupScreen

@Composable
fun DailyCheckupNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
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
                }
            )
        }

        composable(ScreenDest.CommentScreen.route) {
            CommentScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}