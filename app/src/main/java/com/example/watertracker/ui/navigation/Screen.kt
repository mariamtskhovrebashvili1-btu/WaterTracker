package com.example.watertracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.watertracker.R

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.nav_home, Icons.Filled.WaterDrop)
    data object History : Screen("history", R.string.nav_history, Icons.Filled.History)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)

    companion object {
        val items = listOf(Home, History, Settings)
    }
}
