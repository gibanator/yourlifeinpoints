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
import com.example.lifeinpoints.data.target.TargetRepository
import com.example.lifeinpoints.util.toEpochMilliAtEndOfDay
import com.example.lifeinpoints.util.weekDatesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyCheckupViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dailyProgressRepo: DailyCategoryProgressRepository,
    private val dayCompletionRepo: DayCompletionRepository,
    private val commentTemplateRepo: CommentTemplateRepository,
    private val levelRepository: LevelRepository,
    private val targetRepository: TargetRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id = hashCode()
    private val _uiState = MutableStateFlow(DailyCheckupUiState(selectedDate = LocalDate.now()))
    val uiState = _uiState.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent = _levelUpEvent.asStateFlow()

    private val _targetGoalReachedEvent = MutableStateFlow<List<TargetUi>>(emptyList())
    val targetGoalReachedEvent = _targetGoalReachedEvent.asStateFlow()

    private var initJob: Job? = null

    init {
        val dateStr = savedStateHandle.get<String>("date")
        val today = dateStr?.let(LocalDate::parse) ?: LocalDate.now()

        Log.d("VM", "init(), vmId=$id, dateArg=$dateStr, parsed=$today")

        _uiState.update {
            it.copy(
                selectedDate = today,
                currentWeek = mapToUi(weekDatesOf(today), today)
            )
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

        viewModelScope.launch {
            targetRepository.observeAll().collect { entities ->
                val targets = entities
                    .filter { !it.isCompleted }
                    .map { e ->
                        val deadline = e.deadlineMillis?.let { millis ->
                            java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        TargetUi(id = e.id, name = e.name, nameKey = null, days = e.days, daysSelected = e.daysSelected, deadline = deadline)
                    }
                _uiState.update { it.copy(targets = targets) }
            }
        }

        viewModelScope.launch {
            targetRepository.observeCompleted().collect { entities ->
                val completed = entities.map { e ->
                    TargetUi(id = e.id, name = e.name, nameKey = null, days = e.days, daysSelected = e.daysSelected, deadline = null)
                }
                _uiState.update { it.copy(completedTargets = completed) }
            }
        }
    }

    fun refreshCurrentDay() {
        loadDay(_uiState.value.selectedDate)
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
        val visibleCategories = categoryRepository
            .getVisibleCategoriesCreatedBefore(selectedDayMillis)
            .mapNotNull { category ->
                category.id?.let { id ->
                    CategoryUi(id = id, name = category.name, isSystem = false, nameKey = null)
                }
            }

        val visibleIds = visibleCategories.map { category -> category.id }.toSet()


        val templatesByCategory: Map<Int, List<String>> =
            visibleCategories.associate { cat ->
                val list = commentTemplateRepo
                    .getByCategory(cat.id.toLong())
                    .sortedBy { it.position }
                    .map { it.text }
                cat.id to list
            }

        val selectedTargetIds = targetRepository.getSelectedForDate(selected.toString())

        update { state ->
            state.copy(
                selectedDate = selected,
                currentWeek = mapToUi(week, selected),
                selectedCategories = completedCategories
                    .filter { categoryId -> categoryId in visibleIds }
                    .toSet(),
                allCategories = visibleCategories,
                orderedCategories = visibleCategories,
                isDayEnded = isDayCompleted,
                savedComments = savedComments,
                commentDrafts = drafts,
                templatesByCategory = templatesByCategory,
                selectedTargets = selectedTargetIds
            )
        }
        savedStateHandle["selectedDay"] = selected.toString()
    }

    /**
     * Race condition safe function to load a day
     *
     * @param date Date
     */
    private fun loadDay(date: LocalDate) {
        initJob?.cancel()
        initJob = viewModelScope.launch {
            initStateForDay(date)
            savedStateHandle["selectedDay"] = date.toString()
        }
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
        val newSelected = old
            .minusWeeks(1)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

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
        val newSelected = old
            .plusWeeks(1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

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

            // ensure row exists (so we can update it)
            dayCompletionRepo.ensureRow(date)

            val before = dayCompletionRepo.getEntityOrDefault(date)
            val nowCompleted = !before.isCompleted

            if (!nowCompleted) {
                //  un-end day: subtract what this day previously contributed
                val oldXp = before.xpEarned
                if (oldXp != 0) {
                    levelRepository.addXp(-oldXp) // must support negative delta safely
                }

                dayCompletionRepo.setState(date, isCompleted = false, xpEarned = 0)
                levelRepository.updateConsecutiveDays(date, false)

                _uiState.update { it.copy(isDayEnded = false) }
                saveProgress()
                return@launch
            }

            // ✅ end day: compute new XP and apply delta vs old stored XP
            val newXp = calculateXpForCurrentState()
            val delta = newXp - before.xpEarned
            if (delta != 0) {
                levelRepository.addXp(delta)
            }

            dayCompletionRepo.setState(date, isCompleted = true, xpEarned = newXp)
            levelRepository.updateConsecutiveDays(date, true)

            _uiState.update { it.copy(isDayEnded = true) }
            saveProgress()

            val goalReached = targetRepository.getGoalReachedTargets()
            if (goalReached.isNotEmpty()) {
                _targetGoalReachedEvent.value = goalReached.map { e ->
                    TargetUi(id = e.id, name = e.name, nameKey = null, days = e.days, daysSelected = e.daysSelected, deadline = null)
                }
            }
        }
    }
    private suspend fun calculateXpForCurrentState(): Int {
        val state = _uiState.value
        val selectedCount = state.selectedCategories.size
        val totalActive = state.allCategories.size

        val dailyXp = if (totalActive > 0) {
            (100.0 * selectedCount / totalActive).toInt()
        } else 0

        val progress = levelRepository.getOrCreateProgress()
        val bonusXp = if (progress.consecutiveDays >= 3) 50 else 0

        return dailyXp + bonusXp
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
    /**
     * A function for UI
     */
    fun onDaySelected(day: LocalDate) {
        viewModelScope.launch {
            initStateForDay(day)
        }
    }


    fun nextDay() {
        val newSelected = _uiState.value.selectedDate.plusDays(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }

        loadDay(newSelected)
    }

    fun prevDay() {
        val newSelected = _uiState.value.selectedDate.minusDays(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }

        loadDay(newSelected)
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

    fun addTarget(name: String, days: Int, deadline: LocalDate?) {
        viewModelScope.launch {
            targetRepository.addTarget(name, days, deadline)
        }
    }

    fun consumeNextTargetGoalEvent() {
        _targetGoalReachedEvent.update { it.drop(1) }
    }

    fun completeTargetAndNext(id: Int) {
        viewModelScope.launch { targetRepository.completeTarget(id) }
        consumeNextTargetGoalEvent()
    }

    fun extendTargetAndNext(id: Int, additionalDays: Int) {
        viewModelScope.launch { targetRepository.extendTarget(id, additionalDays) }
        consumeNextTargetGoalEvent()
    }

    fun updateTarget(id: Int, name: String, days: Int, deadline: LocalDate?) {
        viewModelScope.launch { targetRepository.updateTarget(id, name, days, deadline) }
    }

    fun deleteTarget(id: Int) {
        viewModelScope.launch { targetRepository.deleteTarget(id) }
    }

    fun toggleTarget(targetId: Int) {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate.toString()
            val nowSelected = targetRepository.toggleSelection(targetId, date)
            val newSet = _uiState.value.selectedTargets.toMutableSet()
            if (nowSelected) newSet.add(targetId) else newSet.remove(targetId)
            _uiState.update { it.copy(selectedTargets = newSet) }
        }
    }

    fun showAiMode() {
        _uiState.update { it.copy(isAiModeVisible = true) }
    }

    fun hideAiMode() {
        _uiState.update { it.copy(isAiModeVisible = false) }
    }

    fun showVoiceRecognition() {
        _uiState.update { it.copy(isVoiceRecognitionVisible = true) }
    }

    fun hideVoiceRecognition() {
        _uiState.update { it.copy(isVoiceRecognitionVisible = false) }
    }
}
