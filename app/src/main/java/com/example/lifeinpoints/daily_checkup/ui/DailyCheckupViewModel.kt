// com.example.lifeinpoints.daily_checkup.ui/DailyCheckupViewModel.kt
package com.example.lifeinpoints.daily_checkup.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryEntity
import com.example.lifeinpoints.data.category.CategoryRepositoryNew
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateRepository
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressEntity
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressRepositoryNew
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import com.example.lifeinpoints.data.level.LevelRepository
import com.example.lifeinpoints.data.remote.ai.AiEvaluateRequest
import com.example.lifeinpoints.data.remote.ai.AiNamedDto
import com.example.lifeinpoints.data.remote.api.AiApi
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import com.example.lifeinpoints.data.target.TargetEntity
import com.example.lifeinpoints.data.target.TargetRepository
import com.example.lifeinpoints.util.toEpochMilliAtEndOfDay
import com.example.lifeinpoints.util.weekDatesOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyCheckupViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositoryNew,
    private val dailyProgressRepo: DailyCategoryProgressRepositoryNew,
    private val dayCompletionRepo: DayCompletionRepository,
    private val commentTemplateRepo: CommentTemplateRepository,
    private val levelRepository: LevelRepository,
    private val targetRepository: TargetRepository,
    private val aiApi: AiApi,
    private val authTokenProvider: AuthTokenProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var dayJob: Job? = null
    private val id = hashCode()
    private val _uiState = MutableStateFlow(DailyCheckupUiState(selectedDate = LocalDate.now()))
    val uiState = _uiState.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<Int?>(null)
    val levelUpEvent = _levelUpEvent.asStateFlow()

    private val _targetGoalReachedEvent = MutableStateFlow<List<TargetUi>>(emptyList())
    val targetGoalReachedEvent = _targetGoalReachedEvent.asStateFlow()


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
        observeDay(today)
        observeCommentTemplates()
        observeTargets()
    }

    private fun observeDay(selected: LocalDate) {
        dayJob?.cancel()

        dayJob = viewModelScope.launch {
            val date = selected.toString()
            val week = weekDatesOf(selected)
            val selectedDayMillis = selected.toEpochMilliAtEndOfDay()

            combine(
                dailyProgressRepo.observeDay(date),
                categoryRepository.observeVisibleCategoriesCreatedBefore(selectedDayMillis),
                dayCompletionRepo.observeDayCompletion(date),
                targetRepository.observeSelectedForDate(date)
            ) { rows, categories, isDayEnded, selectedTargetIds ->
                DayData(rows, categories, isDayEnded, selectedTargetIds.toSet())
            }.collect { (rows, categories, isDayEnded, selectedTargetIds) ->

                val visibleCategories = categories.map { category ->
                    CategoryUi(
                        id = category.localId,
                        name = category.name,
                        isSystem = category.isSystem,
                        nameKey = null
                    )
                }

                val visibleIds = visibleCategories.map { it.id }.toSet()

                val completedCategories = rows
                    .filter { it.value }
                    .map { it.categoryLocalId }
                    .filter { it in visibleIds }
                    .toSet()

                val savedComments = rows.associate { it.categoryLocalId to it.comment }

                val drafts = savedComments.mapValues { (_, comment) ->
                    comment.orEmpty()
                }

                _uiState.update { state ->
                    state.copy(
                        selectedDate = selected,
                        currentWeek = mapToUi(week, selected),
                        selectedCategories = completedCategories,
                        allCategories = visibleCategories,
                        orderedCategories = visibleCategories,
                        savedComments = savedComments,
                        commentDrafts = drafts,
                        isDayEnded = isDayEnded,
                        selectedTargets = selectedTargetIds
                    )
                }
            }
        }
    }

    private fun observeCommentTemplates() {
        viewModelScope.launch {
            commentTemplateRepo.observeAll().collect { allTemplates ->
                val visibleIds = _uiState.value.orderedCategories
                    .map { it.id }
                    .toSet()

                val templatesByCategory = allTemplates
                    .filter { it.categoryLocalId in visibleIds }
                    .groupBy { it.categoryLocalId }
                    .mapValues { (_, list) ->
                        list.sortedBy { it.position }.map { it.text }
                    }

                _uiState.update {
                    it.copy(templatesByCategory = templatesByCategory)
                }
            }
        }
    }

    private fun observeTargets() {
        viewModelScope.launch {
            targetRepository.observeAll().collect { entities ->
                val targets = entities
                    .filter { !it.isCompleted }
                    .map { it.toTargetUi() }

                _uiState.update {
                    it.copy(targets = targets)
                }
            }
        }

        viewModelScope.launch {
            targetRepository.observeCompleted().collect { entities ->
                val completedTargets = entities.map { it.toTargetUi() }

                _uiState.update {
                    it.copy(completedTargets = completedTargets)
                }
            }
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

        observeDay(newSelected)
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

        observeDay(newSelected)
    }

    /**
     * Function to toggle category state
     *
     * @param categoryId id of the category
     */
    fun toggleCategory(categoryId: Int) {
        val newSelection = _uiState.value.selectedCategories.toMutableSet()

        if (categoryId in newSelection) {
            newSelection.remove(categoryId)
        } else {
            newSelection.add(categoryId)
        }

        _uiState.update {
            it.copy(selectedCategories = newSelection)
        }

        saveProgress()
    }

    fun toggleDayEnded() {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate.toString()
            val before = dayCompletionRepo.getCompletedEntity(date)

            if (before != null) {
                val oldXp = before.xpEarned

                if (oldXp != 0) {
                    levelRepository.addXp(-oldXp)
                }

                dayCompletionRepo.unmarkCompleted(date)
                levelRepository.updateConsecutiveDays(date, false)

                _uiState.update { it.copy(isDayEnded = false) }
                saveProgress()
                return@launch
            }

            val newXp = calculateXpForCurrentState()

            if (newXp != 0) {
                levelRepository.addXp(newXp)
            }

            dayCompletionRepo.markCompleted(
                date = date,
                xpEarned = newXp
            )

            levelRepository.updateConsecutiveDays(date, true)

            _uiState.update { it.copy(isDayEnded = true) }
            saveProgress()

            val goalReached = targetRepository.getGoalReachedTargets()
            if (goalReached.isNotEmpty()) {
                _targetGoalReachedEvent.value = goalReached.map { e ->
                    TargetUi(
                        id = e.id,
                        name = e.name,
                        nameKey = null,
                        days = e.days,
                        daysSelected = e.daysSelected,
                        deadline = null
                    )
                }
            }
        }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            val date = state.selectedDate.toString()

            val rows = state.allCategories.map { category ->
                DailyCategoryProgressEntity(
                    date = date,
                    categoryLocalId = category.id,
                    value = category.id in state.selectedCategories,
                    comment = state.commentDrafts[category.id]
                        ?.trim()
                        ?.take(100)
                        ?.ifBlank { null }
                )
            }

            dailyProgressRepo.saveDay(
                date = date,
                rows = rows
            )
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
        _uiState.update { state ->
            state.copy(
                commentDrafts = state.commentDrafts + (categoryId to newText.take(100))
            )
        }
    }

    fun commitCommentsAndLeave(onBack: () -> Unit) {
        saveProgress()
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
        observeDay(day)
    }


    fun nextDay() {
        val newSelected = _uiState.value.selectedDate.plusDays(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }

        observeDay(newSelected)
    }

    fun prevDay() {
        val newSelected = _uiState.value.selectedDate.minusDays(1)

        _uiState.update {
            it.copy(
                selectedDate = newSelected,
                currentWeek = mapToUi(weekDatesOf(newSelected), newSelected)
            )
        }

        observeDay(newSelected)
    }

    fun goToToday() {
        val today = LocalDate.now()

        _uiState.update {
            it.copy(
                selectedDate = today,
                currentWeek = mapToUi(weekDatesOf(today), today)
            )
        }

        observeDay(today)
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
        _uiState.update { it.copy(isAiModeVisible = true, aiError = null) }
    }

    fun hideAiMode() {
        _uiState.update { it.copy(isAiModeVisible = false) }
    }

    /**
     * Отправляет текст дня в нейросеть (через бэкенд) и проставляет отметки 1/0
     * по категориям и целям. XP здесь НЕ начисляется — он считается при «Завершить день».
     *
     * @param dayText текст пользователя из AI-режима
     * @param provider выбранный провайдер (пока единственный доступный — GigaChat)
     */
    fun evaluateDayWithAi(dayText: String, provider: String = "gigachat") {
        val text = dayText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiError = null) }
            try {
                val state = _uiState.value
                val date = state.selectedDate.toString()

                val request = AiEvaluateRequest(
                    provider = provider,
                    date = date,
                    dayText = text,
                    categories = state.allCategories.map { AiNamedDto(it.id, it.name) },
                    targets = state.targets.map { AiNamedDto(it.id, it.name) }
                )

                val auth = authTokenProvider.getAuthorizationHeader()
                val response = aiApi.evaluateDay(auth, request)
                val body = response.body()

                if (!response.isSuccessful || body == null) {
                    _uiState.update {
                        it.copy(isAiLoading = false, aiError = "Ошибка ИИ (${response.code()})")
                    }
                    return@launch
                }

                // 1. Категории 1/0 (ИИ предзаполняет галочки)
                val completedCatIds = body.categories
                    .filter { it.completed }
                    .map { it.categoryId }
                    .toSet()

                // 2. Комментарии от ИИ
                val drafts = body.categories
                    .filter { it.comment.isNotBlank() }
                    .associate { it.categoryId to it.comment.take(100) }

                _uiState.update {
                    it.copy(
                        selectedCategories = completedCatIds,
                        commentDrafts = it.commentDrafts + drafts
                    )
                }

                // 3. Цели: отмечаем выполненные, которые ещё не отмечены за этот день
                val alreadySelected = targetRepository.getSelectedForDate(date)
                body.targets
                    .filter { it.completed && it.targetId !in alreadySelected }
                    .forEach { toggleTarget(it.targetId) }

                // 4. Сохранить отметки категорий
                saveProgress()

                _uiState.update { it.copy(isAiLoading = false) }
                hideAiMode()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAiLoading = false, aiError = e.message ?: "Сетевая ошибка")
                }
            }
        }
    }

    fun showVoiceRecognition() {
        _uiState.update { it.copy(isVoiceRecognitionVisible = true) }
    }

    fun hideVoiceRecognition() {
        _uiState.update { it.copy(isVoiceRecognitionVisible = false) }
    }
}

private data class DayData(
    val rows: List<DailyCategoryProgressEntity>,
    val categories: List<CategoryEntity>,
    val isDayEnded: Boolean,
    val selectedTargetIds: Set<Int>
)

private fun TargetEntity.toTargetUi(): TargetUi {
    return TargetUi(
        id = id,
        name = name,
        nameKey = null,
        days = days,
        daysSelected = daysSelected,
        deadline = deadlineMillis?.let { millis ->
            Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    )
}
