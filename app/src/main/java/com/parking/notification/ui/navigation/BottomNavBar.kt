package com.parking.notification.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.parking.notification.ui.theme.Primary
import com.parking.notification.ui.theme.TextSecondary
import com.parking.notification.ui.theme.BottomNavBackground
import timber.log.Timber

@Composable
fun BottomNavBar(navController: NavController, appStartT0: Long = System.currentTimeMillis()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = BottomNavBackground
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != screen.route) {
                        val tapT0 = System.currentTimeMillis()
                        Timber.i("[TRACE] TAB_SWITCH: tapping '%s' at +%dms (from '%s'), thread=%s",
                            screen.route, System.currentTimeMillis() - appStartT0,
                            currentRoute, Thread.currentThread().name)
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        Timber.i("[TRACE] TAB_SWITCH: navigate() returned at +%dms, took %dms",
                            System.currentTimeMillis() - appStartT0,
                            System.currentTimeMillis() - tapT0)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
