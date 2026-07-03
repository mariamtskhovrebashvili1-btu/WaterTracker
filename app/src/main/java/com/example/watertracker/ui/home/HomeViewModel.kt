package com.example.watertracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.watertracker.data.UserPreferencesRepository
import com.example.watertracker.data.WaterRepository
import com.example.watertracker.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val consumedMl: Int = 0,
    val goalMl: Int = UserPreferencesRepository.DEFAULT_GOAL_ML,
    val progress: Float = 0f
)

class HomeViewModel(
    private val waterRepository: WaterRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        waterRepository.getTodayLogs(),
        preferencesRepository.dailyGoalMl
    ) { logs, goal ->
        val consumed = logs.sumOf { it.amount }
        HomeUiState(
            consumedMl = consumed,
            goalMl = goal,
            progress = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterRepository.addWater(amountMl)
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                HomeViewModel(container.waterRepository, container.userPreferencesRepository)
            }
        }
    }
}
