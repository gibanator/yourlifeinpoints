package com.example.lifeinpoints.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lifeinpoints.util.allDatesOfMonthView
import com.example.lifeinpoints.util.calculateWeekCount
import com.example.lifeinpoints.util.contains
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val today: LocalDate = LocalDate.now()
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            currentMonth = savedStateHandle.get<String>("currentMonth")
                ?.let(YearMonth::parse) ?: YearMonth.now()
        )
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()


    init {
        initMonthlyView(_uiState.value.currentMonth)
    }

    private fun initMonthlyView(currentMonth: YearMonth) {
        update {
            it.copy(
                currentMonth = currentMonth,
                mode = CalendarUiState.Mode.MONTH,
                days = mapDaysToMonthView(currentMonth),
                weeksCount = calculateWeekCount(currentMonth),
            )
        }
        savedStateHandle["currentMonth"] = currentMonth.toString()
    }

    fun initYearlyView() {
        TODO()
    }

    fun mapDaysToMonthView(month: YearMonth): List<DayInMonth> {
        val dates = allDatesOfMonthView(month = month)

        return dates.map { date ->
            DayInMonth(
                isToday = date == today,
                isInCurrentMonth = month.contains(date),
                completionCategory =
                    if (!date.isAfter(today)) DayInMonth.CompletionCategory.COMPLETED
                    else DayInMonth.CompletionCategory.FUTURE,
                isFuture = true,
                date = date
            )
        }
    }

    fun nextMonth() = initMonthlyView(_uiState.value.currentMonth.plusMonths(1))
    fun prevMonth() = initMonthlyView(_uiState.value.currentMonth.minusMonths(1))
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

