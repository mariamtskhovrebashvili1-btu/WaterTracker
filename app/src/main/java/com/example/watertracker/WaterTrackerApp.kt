package com.example.watertracker

import android.app.Application
import com.example.watertracker.di.AppContainer
import com.example.watertracker.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WaterTrackerApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createChannel(this)

        CoroutineScope(Dispatchers.IO).launch {
            val interval = container.userPreferencesRepository.reminderIntervalMinutes.first()
            container.reminderScheduler.schedule(interval)
        }
    }
}
