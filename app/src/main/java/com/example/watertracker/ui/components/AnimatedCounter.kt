package com.example.watertracker.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Animates [targetValue] towards its new value and renders it with [AutoResizeText] so the
 * counter can never overflow its container, no matter how large the number gets.
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayMedium,
    color: Color = Color.Unspecified,
    suffix: String = "",
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 18.sp
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 700),
        label = "animatedCounter"
    )
    AutoResizeText(
        text = "$animatedValue$suffix",
        modifier = modifier,
        style = style,
        color = color,
        maxFontSize = maxFontSize,
        minFontSize = minFontSize
    )
}

/**
 * A single-line [Text] that shrinks its font size until the string fits within the available
 * width, so long numbers/labels never clip or wrap awkwardly.
 */
@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color.Unspecified,
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 12.sp,
    textAlign: TextAlign = TextAlign.Center
) {
    var fontSize by remember(text, maxFontSize) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text, maxFontSize) { mutableStateOf(false) }
    val contentColor = if (color == Color.Unspecified) LocalContentColor.current else color

    Text(
        text = text,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        color = contentColor,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        textAlign = textAlign,
        style = style.copy(fontSize = fontSize),
        onTextLayout = { result ->
            val minPx = minFontSize.value
            if (result.didOverflowWidth && fontSize.value > minPx) {
                fontSize = (fontSize.value - 2f).coerceAtLeast(minPx).sp
            } else {
                readyToDraw = true
            }
        }
    )
}
