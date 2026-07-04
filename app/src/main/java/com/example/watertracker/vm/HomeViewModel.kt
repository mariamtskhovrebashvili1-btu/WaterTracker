package com.example.watertracker.vm

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

// =========================================================================
// ლოგიკის ფენა: HomeViewModel (The Brain of the Home Screen)
// =========================================================================
//
// 💡 რატომ combine()?
// HomeScreen-ს ერთდროულად სჭირდება ორი დამოუკიდებელი წყარო: დღევანდელი
// ჩანაწერები (Room) და დღიური მიზანი (DataStore). combine() აერთიანებს ორივე
// Flow-ს ერთში — როცა ერთ-ერთი მათგანი შეიცვლება, ორივე ისევ გაერთიანდება
// და UI-მდე ერთი მზა HomeUiState მოაღწევს.
//
// 💡 რატომ stateIn()?
// Flow თავისთავად მხოლოდ "მომავალ მოვლენებზე" საუბრობს. stateIn() მას
// გარდაქმნის StateFlow-დ, რომელსაც ყოველთვის აქვს მიმდინარე მნიშვნელობა —
// ეს საჭიროა, რომ Compose-მა collectAsState()-ით პირდაპირ წაიკითხოს.
// =========================================================================

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

    /**
     * იძახება, როცა მომხმარებელი "+100"/"+200"... ღილაკზე აჭერს.
     * UI-მ არ იცის (და არც უნდა იცოდეს) როგორ ინახება ეს ბაზაში — მხოლოდ
     * აცნობებს ViewModel-ს მომხმარებლის განზრახვას.
     */
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterRepository.addWater(amountMl)
        }
    }

    companion object {
        // 💡 factory კონსტრუქტორის ნაცვლად: HomeViewModel-ს სჭირდება Repository-ები,
        // ამიტომ ვერ გამოვიყენებთ პარამეტრების გარეშე viewModel()-ს (`vm: X = viewModel()`) —
        // viewModelFactory ეუბნება სისტემას საიდან უნდა აიღოს ეს დამოკიდებულებები.
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                HomeViewModel(container.waterRepository, container.userPreferencesRepository)
            }
        }
    }
}
