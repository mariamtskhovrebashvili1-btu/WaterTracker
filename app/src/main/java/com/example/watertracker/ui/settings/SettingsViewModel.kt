package com.example.watertracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.watertracker.data.UserPreferencesRepository
import com.example.watertracker.di.AppContainer
import com.example.watertracker.notification.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val dailyGoalMl: Int = UserPreferencesRepository.DEFAULT_GOAL_ML,
    val reminderIntervalMinutes: Int = UserPreferencesRepository.DEFAULT_REMINDER_MINUTES
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.dailyGoalMl,
        preferencesRepository.reminderIntervalMinutes
    ) { goal, interval ->
        SettingsUiState(dailyGoalMl = goal, reminderIntervalMinutes = interval)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun setDailyGoal(goalMl: Int) {
        viewModelScope.launch { preferencesRepository.setDailyGoalMl(goalMl) }
    }

    fun setReminderInterval(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setReminderIntervalMinutes(minutes)
            reminderScheduler.schedule(minutes)
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                SettingsViewModel(container.userPreferencesRepository, container.reminderScheduler)
            }
        }
    }
}
