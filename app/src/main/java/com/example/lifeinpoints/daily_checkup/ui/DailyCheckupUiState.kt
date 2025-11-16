package com.example.lifeinpoints.daily_checkup.ui


import com.example.lifeinpoints.data.category.CategoryRepository
import java.time.LocalDate

data class CategoryUi(
    val id: Int,
    val name: String,
    // color etc
)
/**
 * Represents a day in the week bar.
 *
 * @property dayOfWeek Short name of the day (e.g., "Mon").
 * @property dayOfMonth Number of the day in month (e.g., 9).
 * @property isSelected True, if day is selected now
 */
data class DayForWeekBar(
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val isSelected: Boolean,
    val date: LocalDate
)

data class DailyCheckupUiState (
    val currentWeek: List<DayForWeekBar> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),

    val isDayEnded: Boolean = false,
    val selectedCategories: Set<Int> = emptySet(),
    val allCategories: List<CategoryUi> = emptyList(),
    val isMultiplierMode: Boolean = false,
    val orderedCategories: List<CategoryUI> = emptyList()
)