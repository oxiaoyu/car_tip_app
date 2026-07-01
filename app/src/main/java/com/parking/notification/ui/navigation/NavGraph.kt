package com.parking.notification.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.parking.notification.ui.screen.test.TestScreen
import com.parking.notification.ui.screen.history.HistoryScreen
import com.parking.notification.ui.screen.notification.NotificationListScreen
import com.parking.notification.ui.screen.rule.RuleListScreen
import com.parking.notification.ui.screen.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Test.route,
        modifier = modifier
    ) {
        composable(Screen.Test.route) {
            TestScreen()
        }
        composable(Screen.NotificationList.route) {
            NotificationListScreen()
        }
        composable(Screen.Rules.route) {
            RuleListScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
