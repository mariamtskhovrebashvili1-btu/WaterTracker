package com.example.watertracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watertracker.ui.theme.WaterTrackerTheme
import com.example.watertracker.vm.DayBar

@Composable
fun WaterBarChart(
    bars: List<DayBar>,
    modifier: Modifier = Modifier,
    barAreaHeight: Dp = 180.dp
) {
    val maxValue = (bars.maxOfOrNull { it.totalMl } ?: 0).coerceAtLeast(1)
    val showValueLabels = bars.size <= 10
    val labelStep = if (bars.size > 10) (bars.size / 7).coerceAtLeast(1) else 1

    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .height(barAreaHeight)
                .padding(end = 6.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$maxValue", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            Text("${maxValue / 2}", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            Text("0", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bars.forEachIndexed { index, bar ->
                SingleBar(
                    bar = bar,
                    maxValue = maxValue,
                    barAreaHeight = barAreaHeight,
                    showValueLabel = showValueLabels,
                    showDayLabel = index % labelStep == 0
                )
            }
        }
    }
}

@Composable
private fun RowScope.SingleBar(
    bar: DayBar,
    maxValue: Int,
    barAreaHeight: Dp,
    showValueLabel: Boolean,
    showDayLabel: Boolean
) {
    val targetFraction = bar.totalMl.toFloat() / maxValue.toFloat()
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "barFraction"
    )

    val normalBrush = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    )
    val goalMetBrush = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f))
    )

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showValueLabel) {
            AutoResizeText(
                text = if (bar.totalMl > 0) "${bar.totalMl}" else "",
                style = MaterialTheme.typography.labelSmall,
                minFontSize = 8.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .height(barAreaHeight)
                .fillMaxWidth(0.55f)
        ) {
            val barHeightPx = size.height * animatedFraction
            if (barHeightPx <= 0f) return@Canvas

            val top = size.height - barHeightPx
            val radius = 8.dp.toPx().coerceAtMost(barHeightPx / 2f)
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, top + radius)
                arcTo(Rect(0f, top, 2 * radius, top + 2 * radius), 180f, 90f, false)
                lineTo(size.width - radius, top)
                arcTo(Rect(size.width - 2 * radius, top, size.width, top + 2 * radius), 270f, 90f, false)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path, brush = if (bar.goalMet) goalMetBrush else normalBrush)
        }

        Spacer(modifier = Modifier.height(4.dp))
        if (showDayLabel) {
            AutoResizeText(
                text = bar.label,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minFontSize = 7.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterBarChartWeekPreview() {
    val bars = listOf(
        DayBar("1", "ორშ", 1800, false),
        DayBar("2", "სამ", 2100, true),
        DayBar("3", "ოთხ", 900, false),
        DayBar("4", "ხუთ", 0, false),
        DayBar("5", "პარ", 2400, true),
        DayBar("6", "შაბ", 1500, false),
        DayBar("7", "კვ", 2000, true)
    )
    WaterTrackerTheme {
        WaterBarChart(bars = bars, modifier = Modifier.padding(16.dp))
    }
}
