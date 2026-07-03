package com.example.watertracker.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.watertracker.di.AppContainer
import com.example.watertracker.ui.components.AnimatedCounter
import com.example.watertracker.ui.components.AutoResizeText
import com.example.watertracker.ui.components.QuickAddButton
import com.example.watertracker.ui.components.WaterWaveAnimation
import kotlin.math.roundToInt

private val quickAddAmounts = listOf(100, 200, 300, 500)

@Composable
fun HomeScreen(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(container))
    val uiState by viewModel.uiState.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    )

    val goalReached = uiState.goalMl > 0 && uiState.consumedMl >= uiState.goalMl
    val percentage = if (uiState.goalMl > 0) {
        ((uiState.consumedMl.toFloat() / uiState.goalMl) * 100).roundToInt()
    } else {
        0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "დღევანდელი პროგრესი",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        WaterWaveAnimation(progress = uiState.progress, size = 260.dp) {
            WaveContent(
                consumedMl = uiState.consumedMl,
                goalMl = uiState.goalMl,
                percentage = percentage
            )
        }

        AnimatedVisibility(
            visible = goalReached,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)) + scaleIn(initialScale = 0.85f),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(200)) + scaleOut(targetScale = 0.85f)
        ) {
            CelebrationBanner()
        }

        Text(
            text = "დაამატე წყალი",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickAddAmounts.forEach { amount ->
                QuickAddButton(
                    label = "+$amount",
                    onClick = { viewModel.addWater(amount) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WaveContent(
    consumedMl: Int,
    goalMl: Int,
    percentage: Int
) {
    val textShadow = Shadow(
        color = Color.Black.copy(alpha = 0.25f),
        offset = Offset(0f, 2f),
        blurRadius = 8f
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AnimatedCounter(
            targetValue = consumedMl,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayMedium.copy(shadow = textShadow),
            color = Color.White,
            suffix = " მლ",
            maxFontSize = 40.sp,
            minFontSize = 20.sp
        )
        AutoResizeText(
            text = "მიზანი: $goalMl მლ",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium.copy(shadow = textShadow),
            color = Color.White.copy(alpha = 0.9f),
            minFontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.25f)
        ) {
            Text(
                text = "$percentage%",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = textShadow
                )
            )
        }
    }
}

@Composable
private fun CelebrationBanner() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "მიზანი მიღწეულია! 🎉",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
