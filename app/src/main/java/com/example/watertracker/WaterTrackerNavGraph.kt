package com.example.watertracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.watertracker.di.AppContainer
import com.example.watertracker.ui.routes.Screen
import com.example.watertracker.ui.screens.HistoryScreen
import com.example.watertracker.ui.screens.HomeScreen
import com.example.watertracker.ui.screens.SettingsScreen

// =========================================================================
// ნავიგაციის გრაფი: WaterTrackerNavGraph (The Central Hub)
// =========================================================================
//
// 💡 რა არის ამ ფაილის არქიტექტურული პასუხისმგებლობა?
// ეს ფუნქცია აკავშირებს დამოუკიდებელ ეკრანებს (Home/History/Settings) ერთმანეთთან
// და ხატავს ქვედა ნავიგაციის მენიუს (NavigationBar).
//
// 💡 ოქროს წესი (Separation of Concerns):
// არც HomeScreen-მა, არც HistoryScreen-მა და არც SettingsScreen-მა არ იციან
// ერთმანეთის არსებობის შესახებ და navController არც ერთს არ გადაეცემა
// პარამეტრად. მათ აკავშირებს მხოლოდ ეს ერთი ფაილი.
// =========================================================================

private data class BottomNavItem(
    val route: Screen,
    val labelRes: Int,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_home, Icons.Filled.WaterDrop),
    BottomNavItem(Screen.History, R.string.nav_history, Icons.Filled.History),
    BottomNavItem(Screen.Settings, R.string.nav_settings, Icons.Filled.Settings)
)

@Composable
fun WaterTrackerNavGraph(container: AppContainer) {
    // 1. ნავიგაციის მთავარი კონტროლერი — ინახავს მიმდინარე მდგომარეობას
    // (რომელ ეკრანზე ვართ, რა არის BackStack-ის ისტორია) ეკრანის გადახატვისას.
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                bottomNavItems.forEach { item ->
                    // 💡 TYPE-SAFE შედარება: string-routes-ის ნაცვლად ვამოწმებთ
                    // მიმდინარე დანიშნულების ტიპს (hasRoute) კონკრეტული Screen კლასის მიმართ.
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
                        label = { Text(stringResource(id = item.labelRes)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 2. NavHost არის "კონტეინერი", სადაც ეკრანები ჩაენაცვლება ერთმანეთს.
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> { HomeScreen(container = container) }
            composable<Screen.History> { HistoryScreen(container = container) }
            composable<Screen.Settings> { SettingsScreen(container = container) }
        }
    }
}
