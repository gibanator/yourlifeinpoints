// com.example.lifeinpoints.daily_checkup.ui/DailyCheckupViewModel.kt
package com.example.lifeinpoints.daily_checkup.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressRepository
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import com.example.lifeinpoints.util.toEpochMilliAtEndOfDay
import com.example.lifeinpoints.util.weekDatesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyCheckupViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dailyProgressRepo: DailyCategoryProgressRepository,
    private val dayCompletionRepo: DayCompletionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()  {

    private val _uiState = MutableStateFlow(DailyCheckupUiState(selectedDate = LocalDate.now()))
    val uiState = _uiState.asStateFlow()

    init {
        // Инициализируем системные категории при создании ViewModel
        viewModelScope.launch {
            categoryRepository.initializeSystemCategories()
        }

        val today = savedStateHandle.get<String>("selectedDay")
            ?.let(LocalDate::parse) ?: LocalDate.now()
        viewModelScope.launch {
            initStateForDay(today)
        }

        // Подписываемся на изменения видимых категорий
        viewModelScope.launch {
            categoryRepository.observeVisibleCategories().collect { visibleCategories ->
                // Обновляем состояние, когда меняются видимые категории
                val currentState = _uiState.value
                if (currentState.selectedDate != null) {
                    initStateForDay(currentState.selectedDate)
                }
            }
        }
    }

    private suspend fun initStateForDay(selected: LocalDate) {
        val week = weekDatesOf(selected)
        val completedCategories = dailyProgressRepo
            .getByDate(selected.toString())
            .filter { it.value }
            .map { it.categoryId }
            .toSet()

        // Получаем состояние завершенности дня из базы
        val isDayCompleted = dayCompletionRepo.getDayCompletion(selected.toString())

        // которые созданы не позже этой даты
        val selectedDayMillis = selected.toEpochMilliAtEndOfDay()
        val visibleCategories = categoryRepository.getVisibleCategoriesCreatedBefore(selectedDayMillis)
            .map { CategoryUi(id = it.id, name = it.name) }

        update { state ->
            state.copy(
                selectedDate = selected,
                currentWeek = mapToUi(week, selected),
                selectedCategories = completedCategories.filter { it in visibleCategories.map { it.id } }.toSet(),
                allCategories = visibleCategories,
                orderedCategories = visibleCategories, // Сохраняем упорядоченный список видимых категорий
                isDayEnded = isDayCompleted
            )
        }
        Log.d("Categories", "${visibleCategories.forEach { it.id }}")
        savedStateHandle["selectedDay"] = selected.toString()
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

        viewModelScope.launch {
            initStateForDay(newSelected)
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
        viewModelScope.launch {
            initStateForDay(newSelected)
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
        Log.d("Categories", "${newSelection.sorted()}")
        _uiState.update {
            it.copy(
                selectedCategories = newSelection
            )
        }
    }

    fun toggleDayEnded() {
        viewModelScope.launch {
            val newState = dayCompletionRepo.toggleDayCompletion(_uiState.value.selectedDate.toString())
            _uiState.update {
                it.copy(isDayEnded = newState)
            }
            saveProgress()
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

    /**
     * A function for UI
     */
    fun onDaySelected(day: LocalDate) {
        viewModelScope.launch {
            initStateForDay(day)
        }
    }

    private inline fun update(x: (DailyCheckupUiState) -> DailyCheckupUiState) {
        _uiState.update(x)
    }

    fun saveProgress() {
        viewModelScope.launch {
            val completed = _uiState.value.selectedCategories.toList()
            val incompleted = _uiState.value.allCategories.map {it.id} - completed
            Log.d("Progress", "completed=${completed.sorted()} incompleted=${incompleted.sorted()}")

            dailyProgressRepo.rewriteDayByCategoryIds(
                date = _uiState.value.selectedDate.toString(),
                completedIds = completed,
                incompletedIds = incompleted
            )
        }
    }
}