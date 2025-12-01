// com.example.lifeinpoints.statistics/StatisticsViewModel.kt
package com.example.lifeinpoints.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryRepository
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressRepository
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val dayCompletionRepo: DayCompletionRepository,
    private val dailyProgressRepo: DailyCategoryProgressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _forceRefresh = MutableStateFlow(0) // Триггер для принудительного обновления

    init {
        // Подписываемся на изменения категорий и обновляем статистику
        setupCategorySubscription()
        loadStatistics()
    }

    private fun setupCategorySubscription() {
        // Комбинируем поток категорий с текущим месяцем и триггером обновления
        combine(
            categoryRepository.observeAll(), // Подписываемся на все категории
            _currentMonth,
            _forceRefresh
        ) { categories, month, _ ->
            Triple(categories, month, Unit)
        }
            .debounce(300) // Дебаунс для предотвращения множественных обновлений
            .onEach { (categories, month, _) ->
                // Обновляем статистику при любом изменении категорий
                reloadStatistics(month, categories)
            }
            .launchIn(viewModelScope)
    }

    private fun reloadStatistics(
        month: YearMonth,
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>
    ) {
        viewModelScope.launch {
            try {
                // Загружаем данные за месяц для ВСЕХ категорий
                val monthDataWithAllCategories = loadMonthData(month, allCategories.map { it.id })

                // Определяем релевантные категории для отображения
                val relevantCategoryIds = findRelevantCategories(monthDataWithAllCategories, allCategories)

                // Фильтруем категории для отображения
                val categoriesToShow = allCategories
                    .filter { category -> category.id in relevantCategoryIds }
                    .map { category -> CategoryStats(category.id, category.name, category.isVisible) }

                // Фильтруем данные месяца, оставляя только релевантные категории
                val filteredMonthData = filterMonthDataForRelevantCategories(
                    monthDataWithAllCategories,
                    relevantCategoryIds
                )

                // Рассчитываем сводную статистику
                val summary = calculateSummaryStats(filteredMonthData, month)

                // Обновляем состояние UI
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentMonth = month,
                        monthData = filteredMonthData,
                        categories = categoriesToShow,
                        summary = summary
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadStatistics(month: YearMonth = YearMonth.now()) {
        _uiState.update { it.copy(isLoading = true) }
        _currentMonth.value = month

        viewModelScope.launch {
            try {
                // Получаем текущие категории
                val allCategories = categoryRepository.getAll()
                reloadStatistics(month, allCategories)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun loadMonthData(month: YearMonth, categoryIds: List<Int>): List<DayStatistics> {
        val daysInMonth = month.lengthOfMonth()
        val monthData = mutableListOf<DayStatistics>()

        for (day in 1..daysInMonth) {
            val date = month.atDay(day)
            val dateString = date.toString()
            val isDayCompleted = dayCompletionRepo.getDayCompletion(dateString)

            if (isDayCompleted) {
                val dailyProgress = dailyProgressRepo.getByDate(dateString)
                val progressMap = dailyProgress.associate { it.categoryId to it.value }
                val totalSelected = progressMap.values.count { it }

                val categorySelections = categoryIds.associateWith { categoryId ->
                    progressMap[categoryId] ?: false
                }

                monthData.add(
                    DayStatistics(
                        day = day,
                        date = dateString,
                        totalSelected = totalSelected,
                        categorySelections = categorySelections
                    )
                )
            } else {
                val categorySelections = categoryIds.associateWith { false }
                monthData.add(
                    DayStatistics(
                        day = day,
                        date = dateString,
                        totalSelected = 0,
                        categorySelections = categorySelections
                    )
                )
            }
        }

        return monthData
    }

    private fun findRelevantCategories(
        monthData: List<DayStatistics>,
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>
    ): Set<Int> {
        val relevantCategoryIds = mutableSetOf<Int>()

        // Добавляем все видимые категории
        allCategories
            .filter { it.isVisible }
            .forEach { relevantCategoryIds.add(it.id) }

        // Находим категории, которые были выбраны в завершенные дни
        monthData.forEach { dayData ->
            if (dayData.totalSelected > 0) {
                dayData.categorySelections.forEach { (categoryId, isSelected) ->
                    if (isSelected) {
                        relevantCategoryIds.add(categoryId)
                    }
                }
            }
        }

        return relevantCategoryIds
    }

    private fun filterMonthDataForRelevantCategories(
        monthData: List<DayStatistics>,
        relevantCategoryIds: Set<Int>
    ): List<DayStatistics> {
        return monthData.map { dayData ->
            val filteredSelections = dayData.categorySelections
                .filterKeys { categoryId -> categoryId in relevantCategoryIds }

            val newTotalSelected = filteredSelections.values.count { it }

            dayData.copy(
                totalSelected = newTotalSelected,
                categorySelections = filteredSelections
            )
        }
    }

    private fun calculateSummaryStats(monthData: List<DayStatistics>, month: YearMonth): SummaryStats {
        val completedDays = monthData.count { it.totalSelected > 0 }
        val totalCategoriesSelected = monthData.sumOf { it.totalSelected }
        val averagePerDay = if (completedDays > 0)
            totalCategoriesSelected.toDouble() / completedDays
        else 0.0

        val bestDay = monthData.maxByOrNull { it.totalSelected }

        return SummaryStats(
            totalDays = month.lengthOfMonth(),
            completedDays = completedDays,
            totalCategoriesSelected = totalCategoriesSelected,
            averagePerDay = averagePerDay,
            bestDay = bestDay?.day ?: 0,
            bestDayCount = bestDay?.totalSelected ?: 0
        )
    }

    fun nextMonth() {
        loadStatistics(_currentMonth.value.plusMonths(1))
    }

    fun prevMonth() {
        loadStatistics(_currentMonth.value.minusMonths(1))
    }

    fun toggleViewType() {
        _uiState.update {
            it.copy(
                viewType = when (it.viewType) {
                    ViewType.MONTH -> ViewType.WEEK
                    ViewType.WEEK -> ViewType.MONTH
                }
            )
        }
    }

    fun forceRefresh() {
        _forceRefresh.value = _forceRefresh.value + 1
    }
}