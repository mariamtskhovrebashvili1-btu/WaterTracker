package com.example.watertracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.watertracker.di.AppContainer
import com.example.watertracker.vm.SettingsViewModel

@Composable
fun SettingsScreen(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(container))
    val uiState by viewModel.uiState.collectAsState()

    var goalSlider by remember { mutableFloatStateOf(uiState.dailyGoalMl.toFloat()) }
    var intervalSlider by remember { mutableFloatStateOf(uiState.reminderIntervalMinutes.toFloat()) }

    LaunchedEffect(uiState.dailyGoalMl) { goalSlider = uiState.dailyGoalMl.toFloat() }
    LaunchedEffect(uiState.reminderIntervalMinutes) { intervalSlider = uiState.reminderIntervalMinutes.toFloat() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "პარამეტრები",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "დღიური მიზანი", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${goalSlider.toInt()} მლ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = goalSlider,
                    onValueChange = { goalSlider = it },
                    onValueChangeFinished = { viewModel.setDailyGoal(goalSlider.toInt()) },
                    valueRange = 500f..5000f,
                    steps = 17
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "შეხსენების ინტერვალი", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${intervalSlider.toInt()} წუთი",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = intervalSlider,
                    onValueChange = { intervalSlider = it },
                    onValueChangeFinished = { viewModel.setReminderInterval(intervalSlider.toInt()) },
                    valueRange = 15f..180f,
                    steps = 10
                )
            }
        }
    }
}
