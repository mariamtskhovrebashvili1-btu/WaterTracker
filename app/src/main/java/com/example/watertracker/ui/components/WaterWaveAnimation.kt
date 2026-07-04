package com.example.watertracker.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.watertracker.ui.theme.WaterTrackerTheme
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterWaveAnimation(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "waterLevel"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "waveInfiniteTransition")
    val wavePhaseA by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
        label = "wavePhaseA"
    )
    val wavePhaseB by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6600, easing = LinearEasing)),
        label = "wavePhaseB"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    val waveColorBack = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        )
    )
    val waveColorFront = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val diameter = this.size.minDimension
            val radius = diameter / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val circlePath = Path().apply {
                addOval(Rect(center = center, radius = radius))
            }

            val shadowCenter = Offset(center.x, center.y + 8.dp.toPx())
            val shadowRadius = radius + 12.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.18f), Color.Transparent),
                    center = shadowCenter,
                    radius = shadowRadius
                ),
                radius = shadowRadius,
                center = shadowCenter
            )

            drawCircle(color = trackColor, radius = radius, center = center)

            clipPath(circlePath) {
                val waterLevelY = diameter * (1f - animatedProgress)

                if (animatedProgress > 0f) {
                    val backWave = buildWavePath(
                        width = this.size.width,
                        height = this.size.height,
                        waterLevelY = waterLevelY,
                        amplitude = 12f,
                        wavelength = diameter * 0.9f,
                        phase = wavePhaseB
                    )
                    drawPath(path = backWave, brush = waveColorBack)

                    val frontWave = buildWavePath(
                        width = this.size.width,
                        height = this.size.height,
                        waterLevelY = waterLevelY + 4f,
                        amplitude = 16f,
                        wavelength = diameter * 1.3f,
                        phase = wavePhaseA
                    )
                    drawPath(path = frontWave, brush = waveColorFront)
                }
            }

            drawCircle(
                color = borderColor,
                radius = radius - 2.dp.toPx(),
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
        }
        content()
    }
}

private fun buildWavePath(
    width: Float,
    height: Float,
    waterLevelY: Float,
    amplitude: Float,
    wavelength: Float,
    phase: Float
): Path {
    return Path().apply {
        moveTo(0f, waterLevelY)
        var x = 0f
        val step = 6f
        while (x <= width) {
            val angle = (2 * PI * (x / wavelength)) + phase
            val y = waterLevelY + (amplitude * sin(angle)).toFloat()
            lineTo(x, y)
            x += step
        }
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterWaveAnimationPreview() {
    WaterTrackerTheme {
        WaterWaveAnimation(progress = 0.65f, size = 220.dp)
    }
}
