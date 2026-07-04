package com.example.watertracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.watertracker.data.DailyTotal
import com.example.watertracker.data.WaterLog
import com.example.watertracker.di.AppContainer
import com.example.watertracker.vm.HistoryViewModel
import com.example.watertracker.util.toDisplayDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(container))
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val expandedDate by viewModel.expandedDate.collectAsState()
    val expandedLogs by viewModel.expandedLogs.collectAsState()

    if (dailyTotals.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "ისტორია ჯერ ცარიელია",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dailyTotals, key = { it.date }) { dayTotal ->
            val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
                modifier = Modifier.animateItem()
            ) {
                DayCard(
                    dayTotal = dayTotal,
                    expanded = expandedDate == dayTotal.date,
                    logs = if (expandedDate == dayTotal.date) expandedLogs else emptyList(),
                    onToggle = { viewModel.toggleDay(dayTotal.date) },
                    onDeleteLog = { viewModel.deleteLog(it) }
                )
            }
        }
    }
}

@Composable
private fun DayCard(
    dayTotal: DailyTotal,
    expanded: Boolean,
    logs: List<WaterLog>,
    onToggle: () -> Unit,
    onDeleteLog: (WaterLog) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dayTotal.date.toDisplayDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${dayTotal.total} მლ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    logs.forEach { log ->
                        LogRow(log = log, onDelete = { onDeleteLog(log) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LogRow(log: WaterLog, onDelete: () -> Unit) {
    val time = remember(log.timestamp) {
        Instant.ofEpochMilli(log.timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = time, style = MaterialTheme.typography.bodyMedium)
        Text(text = "${log.amount} მლ", style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "წაშლა",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
