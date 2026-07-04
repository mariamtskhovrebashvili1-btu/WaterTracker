package com.example.watertracker.ui.routes

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object History : Screen

    @Serializable
    data object Statistics : Screen

    @Serializable
    data object Settings : Screen
}
