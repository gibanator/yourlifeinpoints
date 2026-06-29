package com.example.lifeinpoints.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lifeinpoints.R
import com.example.lifeinpoints.Settings.SettingsScreen
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.calendar.ui.CalendarScreen
import com.example.lifeinpoints.categories.AddCategoryScreen
import com.example.lifeinpoints.categories.CategoriesScreen
import com.example.lifeinpoints.categories.CategoryVisibilityScreen
import com.example.lifeinpoints.categories.EditCategoryScreen
import com.example.lifeinpoints.categories.comment_templates.CommentTemplatesCategoriesScreen
import com.example.lifeinpoints.categories.comment_templates.EditCommentTemplatesScreen
import com.example.lifeinpoints.daily_checkup.navigation.DailyCheckupNavHost
import com.example.lifeinpoints.daily_checkup.ui.DailyCheckupViewModel
import com.example.lifeinpoints.login.LoginScreen
import com.example.lifeinpoints.notifications.NotificationSettingsScreen
import com.example.lifeinpoints.registration.RegistrationScreen
import com.example.lifeinpoints.statistics.StatisticsScreen
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    settingsVm: SettingsViewModel,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit = {}
) {
    val registrationSuccessMessage = stringResource(R.string.registration_success)
    val loginSuccessMessage = stringResource(R.string.login_success)
    val categoryAddedMessage = stringResource(R.string.category_added_success)
    val categoryUpdatedMessage = stringResource(R.string.category_updated_success)
    val categoryDeletedMessage = stringResource(R.string.category_deleted_success)

    NavHost(
        navController,
        startDestination = Routes.DAILY_CHECKUP_WITH_ARGS,
        modifier = modifier
    ) {
        composable(
            route = Routes.DAILY_CHECKUP_WITH_ARGS,
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                    defaultValue = LocalDate.now().toString()
                }
            )
        ) { backStackEntry ->
            val vm: DailyCheckupViewModel = hiltViewModel(backStackEntry)
            DailyCheckupNavHost(dailyVm = vm)
        }

        composable(Routes.CALENDAR) {
            CalendarScreen(
                toCertainDate = { date ->
                    navController.navigate(Routes.checkupForDay(date ?: LocalDate.now().toString())) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("registration") {
            RegistrationScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack()
                    onShowSnackbar(registrationSuccessMessage)
                }
            )
        }

        composable("login") {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.popBackStack()
                    onShowSnackbar(loginSuccessMessage)
                }
            )
        }

        composable(Routes.SETTINGS) {
            val isLoggedIn by settingsVm.isLoggedIn.collectAsStateWithLifecycle()
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onCategoriesClick = { navController.navigate("categories") },
                onVisibilityClick = { navController.navigate("category_visibility") },
                onTemplatesClick = { navController.navigate("comment_templates") },
                onRegisterClick = { navController.navigate("registration") },
                onLoginClick = { navController.navigate("login") },
                isLoggedIn = isLoggedIn,
                navController = navController,
                vm = settingsVm
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen()
        }

        composable(Routes.ADD_CATEGORY) {
            AddCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryAdded = {
                    navController.popBackStack()
                    onShowSnackbar(categoryAddedMessage)
                }
            )
        }

        composable("add_category") {
            AddCategoryScreen(
                onBack = { navController.popBackStack() },
                onCategoryAdded = {
                    navController.popBackStack()
                    onShowSnackbar(categoryAddedMessage)
                }
            )
        }

        composable("edit_category/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
            EditCategoryScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onCategoryUpdated = {
                    navController.popBackStack()
                    onShowSnackbar(categoryUpdatedMessage)
                },
                onCategoryDeleted = {
                    navController.popBackStack()
                    onShowSnackbar(categoryDeletedMessage)
                }
            )
        }

        composable(
            route = "edit_templates/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments!!.getInt("categoryId")
            EditCommentTemplatesScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("categories") {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onAddCategory = { navController.navigate("add_category") },
                onEditCategory = { categoryId ->
                    navController.navigate("edit_category/$categoryId")
                },
                onManageVisibility = { navController.navigate("category_visibility") }
            )
        }

        composable("category_visibility") {
            CategoryVisibilityScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("comment_templates") {
            CommentTemplatesCategoriesScreen(
                onBack = { navController.popBackStack() },
                onOpenCategoryTemplates = { categoryId ->
                    navController.navigate("edit_templates/$categoryId")
                }
            )
        }

        composable("notification_settings") {
            NotificationSettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
