package com.example.watertracker.di

import android.content.Context
import com.example.watertracker.data.UserPreferencesRepository
import com.example.watertracker.data.WaterDatabase
import com.example.watertracker.data.WaterRepository
import com.example.watertracker.notification.ReminderScheduler

class AppContainer(context: Context) {
    val waterRepository: WaterRepository by lazy {
        WaterRepository(WaterDatabase.getInstance(context).waterLogDao())
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    val reminderScheduler: ReminderScheduler by lazy {
        ReminderScheduler(context)
    }
}
