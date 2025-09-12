package com.example.lifeinpoints.calendar

import java.time.YearMonth

data class CalendarUiState(
    val mode: Mode = Mode.MONTH,
    val month: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val error: String? = null
){
    enum class Mode {MONTH, YEAR}
}