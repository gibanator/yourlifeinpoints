package com.example.lifeinpoints.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun nextMonth() = update { it.copy(month = it.month.plusMonths(1)) }
    fun prevMonth() = update { it.copy(month = it.month.minusMonths(1)) }
    fun switchMode() = update {
        val newMode = if (it.mode == CalendarUiState.Mode.MONTH)
            CalendarUiState.Mode.YEAR
        else
            CalendarUiState.Mode.MONTH
        it.copy(mode = newMode)
    }

    private inline fun update(x: (CalendarUiState) -> CalendarUiState) {
        _uiState.update(x)
    }

}

