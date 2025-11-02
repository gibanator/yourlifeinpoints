package com.example.lifeinpoints.daily_checkup.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.util.weekDatesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyCheckupViewModel @Inject constructor(
    private val repo: CategoryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()  {
    val _uiState = MutableStateFlow(DailyCheckupUiState(selectedDate = LocalDate.now()))
    val uiState = _uiState.asStateFlow()

    init {
        val today = savedStateHandle.get<String>("selectedDay")
            ?.let(LocalDate::parse) ?: LocalDate.now()
        initStateForDay(today)
    }

    fun initStateForDay(selected: LocalDate) {
        val week = weekDatesOf(selected)

        update {
            it.copy(
                selectedDate = selected,
                currentWeek = mapToUi(week, selected)
            )
        }
        savedStateHandle["selectedDay"] = selected
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



    private inline fun update(x: (DailyCheckupUiState) -> DailyCheckupUiState) {
        _uiState.update(x)
    }

}
