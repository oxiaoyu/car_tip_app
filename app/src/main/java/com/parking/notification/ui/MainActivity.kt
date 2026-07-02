package com.parking.notification.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.parking.notification.ui.navigation.BottomNavBar
import com.parking.notification.ui.navigation.NavGraph
import com.parking.notification.ui.theme.ParkingNotificationTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val t0 = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        val t1 = System.currentTimeMillis()
        super.onCreate(savedInstanceState)
        Timber.i("[TRACE] MAIN_ACTIVITY: super.onCreate() done at +%dms (relative to App.onCreate), thread=%s",
            System.currentTimeMillis() - t0, Thread.currentThread().name)
        setContent {
            Timber.i("[TRACE] MAIN_ACTIVITY: setContent at +%dms", System.currentTimeMillis() - t0)
            ParkingNotificationTheme {
                MainScreen(t0)
            }
        }
        Timber.i("[TRACE] MAIN_ACTIVITY: onCreate complete at +%dms, init=%dms",
            System.currentTimeMillis() - t0, System.currentTimeMillis() - t1)
    }
}

@Composable
fun MainScreen(appStartT0: Long = System.currentTimeMillis()) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        Timber.i("[TRACE] MAIN_SCREEN: first composition rendering at +%dms, thread=%s",
            System.currentTimeMillis() - appStartT0, Thread.currentThread().name)
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, appStartT0) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
