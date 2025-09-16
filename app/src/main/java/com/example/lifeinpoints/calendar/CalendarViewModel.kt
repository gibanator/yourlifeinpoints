package com.example.lifeinpoints.calendar

import androidx.lifecycle.ViewModel
import com.example.lifeinpoints.util.allDatesOfMonth
import com.example.lifeinpoints.util.calculateWeekCount
import com.example.lifeinpoints.util.contains
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth


class CalendarViewModel : ViewModel() {
    private val today: LocalDate = LocalDate.now()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun initMonthlyView(currentMonth: YearMonth) {
        update {
            it.copy(
                currentMonth = currentMonth,
                mode = CalendarUiState.Mode.MONTH,
                days = mapDaysToMonthView(currentMonth),
                weeksCount = calculateWeekCount(currentMonth),
            )
        }
    }

    fun initYearlyView() {
        TODO()
    }

    fun mapDaysToMonthView(month: YearMonth): List<DayInMonth> {
        val dates = allDatesOfMonth(month = month)

        return dates.map { date ->
            DayInMonth(
                isToday = date == today,
                isInCurrentMonth = month.contains(date),
                completionCategory = DayInMonth.CompletionCategory.COMPLETED,
                isFuture = true,
                date = date
            )
        }
    }

    fun nextMonth() = update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }
    fun prevMonth() = update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
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

