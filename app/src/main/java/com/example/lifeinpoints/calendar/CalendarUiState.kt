package com.example.lifeinpoints.calendar

import java.time.LocalDate
import java.time.YearMonth

/**
 * Class that represents day in month view
 *
 */
// com.example.lifeinpoints.calendar/DayInMonth.kt
data class DayInMonth(
    val date: LocalDate,
    val isInCurrentMonth: Boolean,
    val completionCategory: CompletionCategory,
    val isToday: Boolean,
    val isFuture: Boolean
) {
    enum class CompletionCategory{
        NONE,           // День не завершен
        COMPLETED,      // День завершен (кнопка нажата)
        PARTIAL,        // Можно использовать для частичного выполнения
        FUTURE          // Будущие дни
    }
}
data class CalendarUiState(
    val mode: Mode = Mode.MONTH,
    val currentMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val days: List<DayInMonth> = emptyList(),
    val weeksCount: Int = 0
){
    enum class Mode {MONTH, YEAR}
}

