package com.example.subtrack.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.subtrack.ui.add_expense.AddExpenseScreen
import com.example.subtrack.ui.ai_advisor.GeminiAdvisorScreen // <-- הוספנו את האימפורט הזה
import com.example.subtrack.ui.home.HomeScreen
import com.example.subtrack.ui.settings.HistoryScreen
import com.example.subtrack.ui.settings.SettingsScreen
import com.example.subtrack.ui.settings.SubscriptionScreen
import com.example.subtrack.ui.settings.YearlySummaryScreen

object Routes {
    const val HOME = "home"
    const val ADD_EXPENSE = "add_expense"
    const val SETTINGS = "settings"
    const val YEARLY_SUMMARY = "yearly_summary"
    const val HISTORY = "history"
    const val SUBSCRIPTION = "subscription"
    const val AI_ADVISOR = "ai_advisor"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        // --- מסך הבית ---
        composable(Routes.HOME) {
            HomeScreen(
                onAddExpenseClick = {
                    navController.navigate(Routes.ADD_EXPENSE)
                },
                onExpenseClick = { expenseId ->
                    navController.navigate("${Routes.ADD_EXPENSE}?expenseId=$expenseId")
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                // --- חיבור כפתור ה-AI ---
                onAiAdvisorClick = {
                    navController.navigate(Routes.AI_ADVISOR)
                }
            )
        }

        // --- מסך הוספה/עריכה ---
        composable(
            route = "${Routes.ADD_EXPENSE}?expenseId={expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- מסך הגדרות ---
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onYearlySummaryClick = {
                    navController.navigate(Routes.YEARLY_SUMMARY)
                },
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onSubscriptionClick = {
                    navController.navigate(Routes.SUBSCRIPTION)
                }
            )
        }

        composable(Routes.YEARLY_SUMMARY) {
            YearlySummaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SUBSCRIPTION) {
            SubscriptionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.AI_ADVISOR) {
            GeminiAdvisorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}