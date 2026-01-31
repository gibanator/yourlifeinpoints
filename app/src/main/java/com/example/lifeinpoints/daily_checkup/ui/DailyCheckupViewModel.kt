// com.example.lifeinpoints.daily_checkup.ui/DailyCheckupViewModel.kt
package com.example.lifeinpoints.daily_checkup.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateRepository
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressRepository
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import com.example.lifeinpoints.data.level.LevelRepository
import com.example.lifeinpoints.level.LevelViewModel
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
    private val commentTemplateRepo: CommentTemplateRepository,
    private val levelRepository: LevelRepository, // Добавляем репозиторий уровней
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id = hashCode()
    private val _uiState = MutableStateFlow(DailyCheckupUiState(selectedDate = LocalDate.now()))
    val uiState = _uiState.asStateFlow()

    // Для отслеживания повышения уровня
    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent = _levelUpEvent.asStateFlow()

    init {
        // Инициализируем системные категории при создании ViewModel
        viewModelScope.launch {
            categoryRepository.initializeSystemCategories()
        }

        val dateStr = savedStateHandle.get<String>("date")
        val today = dateStr?.let(LocalDate::parse) ?: LocalDate.now()

        Log.d("VM", "init(), vmId=$id, dateArg=$dateStr, parsed=$today")

        // Подписываемся на изменения видимых категорий
        viewModelScope.launch {
            categoryRepository.observeVisibleCategories().collect { visibleCategories ->
                // Обновляем состояние, когда меняются видимые категории
                initStateForDay(today)
            }
        }

        viewModelScope.launch {
            commentTemplateRepo.observeAll().collect { allTemplates ->
                val visibleIds = _uiState.value.orderedCategories.map { it.id }.toSet()

                val map: Map<Int, List<String>> =
                    allTemplates
                        .filter { it.categoryId.toInt() in visibleIds }
                        .groupBy { it.categoryId.toInt() }
                        .mapValues { (_, list) ->
                            list.sortedBy { it.position }.map { it.text }
                        }

                _uiState.update { it.copy(templatesByCategory = map) }
            }
        }
    }

    /**
     * Function for initializing the state for a date. Gets the data for day from the database and
     * applies it to UiState.
     *
     * @param selected Date to initialize
     */
    private suspend fun initStateForDay(selected: LocalDate) {
        Log.d("VM", "initStateForDay() CALLED, vmId=$id, day=$selected")
        val week = weekDatesOf(selected)

        val rows = dailyProgressRepo.getByDate(selected.toString())
        val completedCategories: Set<Int> =
            rows
                .filter { it.value }
                .map { it.categoryId }
                .toSet()

        val savedComments: Map<Int, String?> =
            rows.associate { it.categoryId to it.comment }

        val drafts: Map<Int, String> =
            savedComments.mapValues { (_, c) -> c.orEmpty() }

        // Получаем состояние завершенности дня из базы
        val isDayCompleted = dayCompletionRepo.getDayCompletion(selected.toString())

        // которые созданы не позже этой даты
        val selectedDayMillis = selected.toEpochMilliAtEndOfDay()
        val visibleCategories = categoryRepository.getVisibleCategoriesCreatedBefore(selectedDayMillis)
            .map { CategoryUi(id = it.id, name = it.name) }

        val templatesByCategory: Map<Int, List<String>> =
            visibleCategories.associate { cat ->
                val list = commentTemplateRepo
                    .getByCategory(cat.id.toLong())
                    .sortedBy { it.position }
                    .map { it.text }
                cat.id to list
            }

        update { state ->
            state.copy(
                selectedDate = selected,
                currentWeek = mapToUi(week, selected),
                selectedCategories = completedCategories.filter { it in visibleCategories.map { it.id } }.toSet(),
                allCategories = visibleCategories,
                orderedCategories = visibleCategories, // Сохраняем упорядоченный список видимых категорий
                isDayEnded = isDayCompleted,
                savedComments = savedComments,
                commentDrafts = drafts,
                templatesByCategory = templatesByCategory
            )
        }
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
            val date = _uiState.value.selectedDate.toString()
            val newState = dayCompletionRepo.toggleDayCompletion(date)

            _uiState.update {
                it.copy(isDayEnded = newState)
            }

            if (newState) {
                // Обновляем последовательные дни и начисляем XP
                levelRepository.updateConsecutiveDays(date, true)
                calculateAndAddXp()
            } else {
                // Сбрасываем последовательные дни
                levelRepository.updateConsecutiveDays(date, false)
            }

            saveProgress()
        }
    }

    private suspend fun calculateAndAddXp() {
        val state = _uiState.value
        val selectedCount = state.selectedCategories.size
        val totalActive = state.allCategories.size

        // Базовые XP за день по формуле: 100 * (m/n)
        val dailyXp = if (totalActive > 0) {
            (100.0 * selectedCount / totalActive).toInt()
        } else {
            0
        }

        // Получаем текущий прогресс для проверки последовательных дней
        val progress = levelRepository.getOrCreateProgress()

        // Бонус за 3+ последовательных дней (k = 50)
        val bonusXp = if (progress.consecutiveDays >= 3) 50 else 0

        // Общее количество XP
        val totalXp = dailyXp + bonusXp

        if (totalXp > 0) {
            // Сохраняем старый уровень для проверки
            val oldLevel = progress.currentLevel

            // Начисляем XP
            val updatedProgress = levelRepository.addXp(totalXp)

            // Проверяем, повысился ли уровень
            if (updatedProgress.currentLevel > oldLevel) {
                // Триггерим событие повышения уровня
                _levelUpEvent.value = updatedProgress.currentLevel
            }

            Log.d("XP", "Начислено XP: $totalXp (базовых: $dailyXp, бонус: $bonusXp)")
        }
    }

    fun onCommentChanged(categoryId: Int, newText: String) {
        _uiState.update { s ->
            s.copy(
                commentDrafts = s.commentDrafts + (categoryId to newText.take(100))
            )
        }
    }

    fun commitCommentsAndLeave(onBack: () -> Unit) {
        val s = _uiState.value
        val date = s.selectedDate.toString()

        viewModelScope.launch {
            s.commentDrafts.forEach { (categoryId, raw) ->
                val newClean = raw.trim().take(100)
                val oldClean = s.savedComments[categoryId]?.trim().orEmpty()

                when {
                    newClean == oldClean -> Unit
                    newClean.isNotEmpty() -> dailyProgressRepo.updateComment(categoryId, date, newClean)
                    oldClean.isNotEmpty() -> dailyProgressRepo.updateComment(categoryId, date, null) // delete
                    else -> Unit
                }
            }
        }

        onBack()
    }

    fun getTemplatesForCategory(categoryId: Int): List<String> =
        _uiState.value.templatesByCategory[categoryId].orEmpty()

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

    fun goToToday() {
        val today = LocalDate.now()

        _uiState.update {
            it.copy(
                selectedDate = today,
                currentWeek = mapToUi(weekDatesOf(today), today)
            )
        }

        viewModelScope.launch {
            initStateForDay(today)
            savedStateHandle["selectedDay"] = today.toString()
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

    // Метод для сброса события повышения уровня (вызывается после показа диалога)
    fun levelUpEventConsumed() {
        _levelUpEvent.value = null
    }
}