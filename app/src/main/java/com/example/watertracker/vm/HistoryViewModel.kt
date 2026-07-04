package com.example.watertracker.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.watertracker.data.DailyTotal
import com.example.watertracker.data.WaterLog
import com.example.watertracker.data.WaterRepository
import com.example.watertracker.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: WaterRepository) : ViewModel() {

    val dailyTotals: StateFlow<List<DailyTotal>> = repository.getAllGroupedByDate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _expandedDate = MutableStateFlow<String?>(null)
    val expandedDate: StateFlow<String?> = _expandedDate

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val expandedLogs: StateFlow<List<WaterLog>> = _expandedDate.flatMapLatest { date ->
        if (date == null) kotlinx.coroutines.flow.flowOf(emptyList()) else repository.getLogsByDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleDay(date: String) {
        _expandedDate.value = if (_expandedDate.value == date) null else date
    }

    fun deleteLog(log: WaterLog) {
        viewModelScope.launch { repository.deleteLog(log) }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer { HistoryViewModel(container.waterRepository) }
        }
    }
}
