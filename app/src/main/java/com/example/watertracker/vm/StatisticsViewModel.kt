package com.example.watertracker.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.watertracker.data.DailyTotal
import com.example.watertracker.data.UserPreferencesRepository
import com.example.watertracker.data.WaterRepository
import com.example.watertracker.di.AppContainer
import com.example.watertracker.util.daysAgoDateString
import com.example.watertracker.util.toDayOfMonthLabel
import com.example.watertracker.util.toShortWeekdayLabel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class StatsRange(val days: Int, val labelText: String) {
    Week(days = 7, labelText = "7 დღე"),
    Month(days = 30, labelText = "30 დღე")
}

data class DayBar(
    val date: String,
    val label: String,
    val totalMl: Int,
    val goalMet: Boolean
)

data class StatisticsUiState(
    val range: StatsRange = StatsRange.Week,
    val bars: List<DayBar> = emptyList(),
    val averageDailyMl: Int = 0,
    val totalMl: Int = 0,
    val goalMetDays: Int = 0,
    val goalMl: Int = UserPreferencesRepository.DEFAULT_GOAL_ML
)

class StatisticsViewModel(
    private val waterRepository: WaterRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _range = MutableStateFlow(StatsRange.Week)
    val range: StateFlow<StatsRange> = _range

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatisticsUiState> = combine(
        _range,
        preferencesRepository.dailyGoalMl
    ) { range, goalMl -> range to goalMl }
        .flatMapLatest { (range, goalMl) ->
            val sinceDate = daysAgoDateString(range.days - 1)
            waterRepository.getDailyTotalsSince(sinceDate).map { totals ->
                buildUiState(range, goalMl, totals)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )

    fun selectRange(newRange: StatsRange) {
        _range.value = newRange
    }

    private fun buildUiState(range: StatsRange, goalMl: Int, totals: List<DailyTotal>): StatisticsUiState {
        val totalByDate = totals.associate { it.date to it.total }

        val bars = (range.days - 1 downTo 0).map { daysAgo ->
            val date = daysAgoDateString(daysAgo)
            val totalMl = totalByDate[date] ?: 0
            DayBar(
                date = date,
                label = if (range == StatsRange.Week) date.toShortWeekdayLabel() else date.toDayOfMonthLabel(),
                totalMl = totalMl,
                goalMet = goalMl > 0 && totalMl >= goalMl
            )
        }

        val totalMl = bars.sumOf { it.totalMl }
        val averageDailyMl = if (bars.isNotEmpty()) totalMl / bars.size else 0
        val goalMetDays = bars.count { it.goalMet }

        return StatisticsUiState(
            range = range,
            bars = bars,
            averageDailyMl = averageDailyMl,
            totalMl = totalMl,
            goalMetDays = goalMetDays,
            goalMl = goalMl
        )
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                StatisticsViewModel(container.waterRepository, container.userPreferencesRepository)
            }
        }
    }
}
