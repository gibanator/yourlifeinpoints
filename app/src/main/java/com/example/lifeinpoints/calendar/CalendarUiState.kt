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

data class MonthUi(
    val month: YearMonth,
    val days: List<DayInMonth>,
    val weeksCount: Int
)

//data class CalendarUiState(
//    val mode: Mode = Mode.MONTH,
//    val isLoading: Boolean = false,
//    val error: String? = null,
//
//    val currentMonth: YearMonth = YearMonth.now(),
////    val monthDays: List<DayInMonth> = emptyList(),
////    val monthWeeksCount: Int = 0,
//
//    val currentYear: Int = YearMonth.now().year,
////    val yearMonths: List<MonthUi> = emptyList()
//){
//    enum class Mode {MONTH, YEAR}
//}

data class CalendarUiState(
    val mode: Mode = Mode.MONTH,
    val selectedMonth: YearMonth = YearMonth.now(),
    val yearCursor: YearMonth = YearMonth.now(),
){
    enum class Mode {MONTH, YEAR}
}

