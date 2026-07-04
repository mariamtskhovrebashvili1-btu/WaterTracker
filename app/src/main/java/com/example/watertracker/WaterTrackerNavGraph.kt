package com.example.watertracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.watertracker.di.AppContainer
import com.example.watertracker.ui.components.AutoResizeText
import com.example.watertracker.ui.routes.Screen
import com.example.watertracker.ui.screens.HistoryScreen
import com.example.watertracker.ui.screens.HomeScreen
import com.example.watertracker.ui.screens.SettingsScreen
import com.example.watertracker.ui.screens.StatisticsScreen

private data class BottomNavItem(
    val route: Screen,
    val labelRes: Int,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_home, Icons.Filled.WaterDrop),
    BottomNavItem(Screen.History, R.string.nav_history, Icons.Filled.History),
    BottomNavItem(Screen.Statistics, R.string.nav_statistics, Icons.Filled.BarChart),
    BottomNavItem(Screen.Settings, R.string.nav_settings, Icons.Filled.Settings)
)

@Composable
fun WaterTrackerNavGraph(container: AppContainer) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                bottomNavItems.forEach { item ->
                    val selected = navBackStackEntry?.destination
                        ?.hasRoute(item.route::class) == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = null) },
                        label = {
                            AutoResizeText(
                                text = stringResource(id = item.labelRes),
                                style = MaterialTheme.typography.labelMedium,
                                minFontSize = 8.sp
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> { HomeScreen(container = container) }
            composable<Screen.History> { HistoryScreen(container = container) }
            composable<Screen.Statistics> { StatisticsScreen(container = container) }
            composable<Screen.Settings> { SettingsScreen(container = container) }
        }
    }
}
