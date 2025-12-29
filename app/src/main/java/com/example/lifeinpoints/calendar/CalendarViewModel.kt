// com.example.lifeinpoints.calendar/CalendarViewModel.kt
package com.example.lifeinpoints.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import com.example.lifeinpoints.util.allDatesOfMonthView
import com.example.lifeinpoints.util.calculateWeekCount
import com.example.lifeinpoints.util.contains
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dayCompletionRepo: DayCompletionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        CalendarUiState(
            mode = savedStateHandle.get<String>("mode")
                ?.let { runCatching { CalendarUiState.Mode.valueOf(it) }.getOrNull() }
                ?: CalendarUiState.Mode.MONTH,

            selectedMonth = savedStateHandle.get<String>("selectedMonth")
                ?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
                ?: YearMonth.now(),

            yearCursor = savedStateHandle.get<String>("yearCursor")
                ?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
                ?: YearMonth.now()
        )
    )

    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val yearFlow = uiState
        .map { it.yearCursor.year }
        .distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val statusMapFlow: StateFlow<Map<LocalDate, DayInMonth.CompletionCategory>> =
        yearFlow
            .flatMapLatest { year -> dayCompletionRepo.observeYear(year) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val monthUi: StateFlow<MonthUi> =
        combine(
            uiState.map { it.selectedMonth }.distinctUntilChanged(),
            statusMapFlow
        ) { month, map ->
            buildMonthUi(month, map)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
            buildMonthUi(YearMonth.now(), emptyMap())
        )

    val yearUi: StateFlow<List<MonthUi>> =
        combine(
            uiState.map { it.yearCursor.year }.distinctUntilChanged(),
            statusMapFlow
        ) { year, map ->
            (1..12).map { month ->
                buildMonthUi(YearMonth.of(year, month), map)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    private fun buildMonthUi(
        month: YearMonth,
        statusMap: Map<LocalDate, DayInMonth.CompletionCategory>
    ): MonthUi {
        val today = LocalDate.now()
        val dates = allDatesOfMonthView(month)

        val days = dates.map {date ->
            val inMonth = month.contains(date)
            val isFuture = date.isAfter(today)

            val category =
                if (isFuture) DayInMonth.CompletionCategory.FUTURE
                else statusMap[date] ?: DayInMonth.CompletionCategory.NONE

            DayInMonth(
                date = date,
                isInCurrentMonth = inMonth,
                completionCategory = category,
                isToday = date == today,
                isFuture = isFuture
            )
        }
        return MonthUi(
            month = month,
            days = days,
            weeksCount = calculateWeekCount(month)
        )
    }

    fun openYear() {
        _uiState.update {
            it.copy(
                mode = CalendarUiState.Mode.YEAR,
                yearCursor = it.selectedMonth // anchor year to remembered month
            )
        }
        saveState(_uiState.value)
    }

    /** Go back to remembered month */
    fun openMonth() {
        _uiState.update {
            it.copy(mode = CalendarUiState.Mode.MONTH)
        }
        saveState(_uiState.value)
    }

    /** Select a specific month (from YEAR view) */
    fun openMonth(month: YearMonth) {
        _uiState.update {
            it.copy(
                mode = CalendarUiState.Mode.MONTH,
                selectedMonth = month,
                yearCursor = YearMonth.of(month.year, 1)
            )
        }
        saveState(_uiState.value)
    }

    fun openYear(year: Int) {
        _uiState.update {
            it.copy(
                mode = CalendarUiState.Mode.YEAR,
                yearCursor = it.selectedMonth
            )
        }
    }

    fun nextMonth() = openMonth(_uiState.value.selectedMonth.plusMonths(1))
    fun prevMonth() = openMonth(_uiState.value.selectedMonth.minusMonths(1))

    fun nextYear() = _uiState.update { it.copy(yearCursor = it.yearCursor.plusYears(1)) }
    fun prevYear() = _uiState.update { it.copy(yearCursor = it.yearCursor.minusYears(1)) }

    private fun saveState(state: CalendarUiState) {
        savedStateHandle["mode"] = state.mode.name
        savedStateHandle["selectedMonth"] = state.selectedMonth.toString()
        savedStateHandle["yearCursor"] = state.yearCursor.toString()
    }

    private inline fun update(x: (CalendarUiState) -> CalendarUiState) {
        _uiState.update(x)
    }
}