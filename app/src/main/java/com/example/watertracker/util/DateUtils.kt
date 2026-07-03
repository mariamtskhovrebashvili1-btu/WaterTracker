package com.example.watertracker.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ISO_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun todayDateString(): String = LocalDate.now().format(ISO_DATE_FORMATTER)

fun Long.toDateString(): String =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate().format(ISO_DATE_FORMATTER)

fun String.toDisplayDate(): String {
    val date = LocalDate.parse(this, ISO_DATE_FORMATTER)
    val today = LocalDate.now()
    return when (date) {
        today -> "დღეს"
        today.minusDays(1) -> "გუშინ"
        else -> {
            val day = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.FULL, Locale("ka"))
            "$day $month"
        }
    }
}
