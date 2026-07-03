package com.example.watertracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "water_tracker_settings")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val DAILY_GOAL_ML = intPreferencesKey("daily_goal_ml")
        val REMINDER_INTERVAL_MINUTES = intPreferencesKey("reminder_interval_minutes")
    }

    val dailyGoalMl: Flow<Int> = context.dataStore.data.map { it[Keys.DAILY_GOAL_ML] ?: DEFAULT_GOAL_ML }

    val reminderIntervalMinutes: Flow<Int> =
        context.dataStore.data.map { it[Keys.REMINDER_INTERVAL_MINUTES] ?: DEFAULT_REMINDER_MINUTES }

    suspend fun setDailyGoalMl(goalMl: Int) {
        context.dataStore.edit { it[Keys.DAILY_GOAL_ML] = goalMl }
    }

    suspend fun setReminderIntervalMinutes(minutes: Int) {
        context.dataStore.edit { it[Keys.REMINDER_INTERVAL_MINUTES] = minutes }
    }

    companion object {
        const val DEFAULT_GOAL_ML = 2000
        const val DEFAULT_REMINDER_MINUTES = 60
    }
}
