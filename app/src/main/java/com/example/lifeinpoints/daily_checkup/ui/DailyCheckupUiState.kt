package com.example.lifeinpoints.daily_checkup.ui


import java.time.LocalDate

data class CategoryUi(
    val id: Int,
    val name: String,
    val isSystem: Boolean,
    val nameKey: String?
    // color etc
)

data class TargetUi(
    val id: Int,
    val name: String,
    val nameKey: String?,
    val days: Int,
    val daysSelected: Int = 0,
    val deadline: LocalDate?
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
    val orderedCategories: List<CategoryUi> = emptyList(),

    val commentDrafts: Map<Int, String> = emptyMap(),
    val savedComments: Map<Int, String?> = emptyMap(),

    val templatesByCategory: Map<Int, List<String>> = emptyMap(),

    val isAiModeVisible: Boolean = false,
    val isAiLoading: Boolean = false,
    val aiError: String? = null,

    val targets: List<TargetUi> = emptyList(),
    val selectedTargets: Set<Int> = emptySet(),
    val completedTargets: List<TargetUi> = emptyList(),

    val isVoiceRecognitionVisible: Boolean = false
)
