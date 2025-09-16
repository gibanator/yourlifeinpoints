package com.example.lifeinpoints.daily_checkup.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import com.example.lifeinpoints.util.weekDatesOf
import kotlinx.coroutines.flow.update

@HiltViewModel
class NewDailyCheckupViewModel @Inject constructor() : ViewModel()  {

    val today: LocalDate = LocalDate.now(ZoneId.systemDefault())

    val _uiState = MutableStateFlow(initState(today))
    val uiState = _uiState.asStateFlow()


    private fun initState(selected: LocalDate): DailyCheckupUiState {
        val week = weekDatesOf(selected)
        return DailyCheckupUiState(
            mapToUi(
                dates = week,
                selectedDay = selected
            ),
        )
    }

    /**
     * Helper function to map week in LocalDate representation to suitable for UI
     *
     * @param dates Week list of days
     * @param selectedDay Day of the week which is selected currently
     */
    private fun mapToUi(dates: List<LocalDate>, selectedDay: LocalDate?): List<DayForWeekBar> =
        dates.map { day ->
            DayForWeekBar(
                dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                dayOfMonth = day.dayOfMonth,
                isSelected = (day == selectedDay),
                date = day
            )
        }

    /**
     * Function to move current day to the day selected on grid
     *
     * @param date The target date
     */
    fun onDaySelected(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                currentWeek = mapToUi(weekDatesOf(date), date)
                )
        }
        resetCategories()
    }


    fun toPrevWeek() {
        val old = _uiState.value.selectedDate
        val newSelected = old.minusWeeks(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }
    }

    fun toNextWeek() {
        val old = _uiState.value.selectedDate
        val newSelected = old.plusWeeks(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }
    }

    /**
     * Function to toggle category state
     *
     * @param index Index of the category
     */
    fun toggleCategory(index: Int) {
        val newSelection = _uiState.value.selectedCategories.toMutableSet()
        if (newSelection.contains(index)) {
            newSelection.remove(index)
        } else {
            newSelection.add(index)
        }
        _uiState.update {
            it.copy(
                selectedCategories = newSelection
            )
        }
    }

    fun toggleDayEnded() {
        _uiState.update {
            it.copy(isDayEnded = !it.isDayEnded)
        }
    }

    fun toggleMultiplierMode() {
        _uiState.update {
            it.copy(isMultiplierMode = !it.isMultiplierMode)
        }
    }

    private fun resetCategories() {
        _uiState.update {
            it.copy(
                isMultiplierMode = false,
                selectedCategories = emptySet(),
                isDayEnded = false
            )
        }
    }
}
