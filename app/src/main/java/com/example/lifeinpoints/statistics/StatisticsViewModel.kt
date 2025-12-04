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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.Year
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val dayCompletionRepo: DayCompletionRepository,
    private val dailyProgressRepo: DailyCategoryProgressRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _forceRefresh = MutableStateFlow(0)

    private val dateFormatter = DateTimeFormatter.ofPattern("d")
    private val weekDayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)

    init {
        setupCategorySubscription()
        setupDayCompletionSubscription() // Добавляем подписку на изменения завершения дней
        loadStatistics()
    }

    private fun setupCategorySubscription() {
        combine(
            categoryRepository.observeAll(),
            _forceRefresh
        ) { categories, _ ->
            categories
        }
            .debounce(300)
            .onEach { categories ->
                reloadStatistics(categories)
            }
            .launchIn(viewModelScope)
    }

    // Добавляем новую подписку на изменения завершения дней
    private fun setupDayCompletionSubscription() {
        dayCompletionRepo.observeAllChanges()
            .debounce(500) // Небольшая задержка, чтобы избежать множественных перезагрузок
            .onEach {
                // При любом изменении завершения дня перезагружаем статистику
                loadStatistics()
            }
            .launchIn(viewModelScope)
    }

    private fun reloadStatistics(
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>
    ) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                when (currentState.viewType) {
                    ViewType.MONTH -> {
                        // Загружаем месячные данные
                        val monthData = loadMonthData(currentState.currentMonth, allCategories.map { it.id })
                        val relevantCategoryIds = findRelevantCategories(monthData, allCategories)
                        val categoriesToShow = filterCategories(allCategories, relevantCategoryIds)
                        val filteredMonthData = filterMonthDataForRelevantCategories(monthData, relevantCategoryIds)
                        val monthSummary = calculateMonthSummaryStats(filteredMonthData, currentState.currentMonth)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                monthData = filteredMonthData,
                                categories = categoriesToShow,
                                monthSummary = monthSummary
                            )
                        }
                    }
                    ViewType.WEEK -> {
                        // Загружаем недельные данные
                        val weekData = loadWeekData(currentState.currentWeekStart, allCategories.map { it.id })
                        val relevantCategoryIds = findRelevantCategories(weekData, allCategories)
                        val categoriesToShow = filterCategories(allCategories, relevantCategoryIds)
                        val filteredWeekData = filterWeekDataForRelevantCategories(weekData, relevantCategoryIds)
                        val weekSummary = calculateWeekSummaryStats(filteredWeekData, currentState.currentWeekStart)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                weekData = filteredWeekData,
                                categories = categoriesToShow,
                                weekSummary = weekSummary
                            )
                        }
                    }
                    ViewType.YEAR -> {
                        // Загружаем годовые данные
                        val yearData = loadYearData(currentState.currentYear, allCategories.map { it.id })
                        val relevantCategoryIds = findRelevantCategoriesForYear(yearData, allCategories)
                        val categoriesToShow = filterCategories(allCategories, relevantCategoryIds)
                        val filteredYearData = filterYearDataForRelevantCategories(yearData, relevantCategoryIds)
                        val yearSummary = calculateYearSummaryStats(filteredYearData, currentState.currentYear)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                yearData = filteredYearData,
                                categories = categoriesToShow,
                                yearSummary = yearSummary
                            )
                        }
                    }
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

    // Загрузка данных за год
    private suspend fun loadYearData(
        year: Year,
        categoryIds: List<Int>
    ): List<MonthStatistics> {
        val yearData = mutableListOf<MonthStatistics>()

        // Проходим по всем месяцам года (1-12)
        for (monthNumber in 1..12) {
            val yearMonth = YearMonth.of(year.value, monthNumber)
            val monthName = yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

            // Инициализируем мапу для сумм по категориям
            val categorySums = mutableMapOf<Int, Int>()
            categoryIds.forEach { categoryId ->
                categorySums[categoryId] = 0
            }

            var totalSelected = 0

            // Проходим по всем дням месяца
            val daysInMonth = yearMonth.lengthOfMonth()
            for (day in 1..daysInMonth) {
                val date = yearMonth.atDay(day)
                val dateString = date.toString()
                val isDayCompleted = dayCompletionRepo.getDayCompletion(dateString)

                if (isDayCompleted) {
                    val dailyProgress = dailyProgressRepo.getByDate(dateString)
                    val progressMap = dailyProgress.associate { it.categoryId to it.value }

                    // Суммируем выбранные категории для этого дня
                    progressMap.forEach { (categoryId, isSelected) ->
                        if (isSelected && categoryId in categoryIds) {
                            categorySums[categoryId] = categorySums[categoryId]!! + 1
                            totalSelected++
                        }
                    }
                }
            }

            yearData.add(
                MonthStatistics(
                    monthNumber = monthNumber,
                    monthName = monthName,
                    totalSelected = totalSelected,
                    categorySums = categorySums
                )
            )
        }

        return yearData
    }

    // Находим релевантные категории для года
    private fun findRelevantCategoriesForYear(
        yearData: List<MonthStatistics>,
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>
    ): Set<Int> {
        val relevantCategoryIds = mutableSetOf<Int>()

        // Добавляем все видимые категории
        allCategories
            .filter { it.isVisible }
            .forEach { relevantCategoryIds.add(it.id) }

        // Находим категории, которые были выбраны хотя бы в одном месяце
        yearData.forEach { monthData ->
            monthData.categorySums.forEach { (categoryId, sum) ->
                if (sum > 0) {
                    relevantCategoryIds.add(categoryId)
                }
            }
        }

        return relevantCategoryIds
    }

    // Фильтруем годовые данные, оставляя только релевантные категории
    private fun filterYearDataForRelevantCategories(
        yearData: List<MonthStatistics>,
        relevantCategoryIds: Set<Int>
    ): List<MonthStatistics> {
        return yearData.map { monthData ->
            val filteredSums = monthData.categorySums
                .filterKeys { categoryId -> categoryId in relevantCategoryIds }

            // Пересчитываем общую сумму после фильтрации
            val newTotalSelected = filteredSums.values.sum()

            monthData.copy(
                totalSelected = newTotalSelected,
                categorySums = filteredSums
            )
        }
    }

    // Расчет статистики за год
    private fun calculateYearSummaryStats(
        yearData: List<MonthStatistics>,
        year: Year
    ): YearSummaryStats {
        // Месяцы, в которых был хотя бы один завершенный день (totalSelected > 0)
        val completedMonths = yearData.count { it.totalSelected > 0 }

        // Общее количество выбранных категорий за год
        val totalCategoriesSelected = yearData.sumOf { it.totalSelected }

        // Среднее количество выбранных категорий в месяц (только для месяцев с данными)
        val averagePerMonth = if (completedMonths > 0)
            totalCategoriesSelected.toDouble() / completedMonths
        else 0.0

        // Лучший месяц
        val bestMonthData = yearData.maxByOrNull { it.totalSelected }
        val bestMonth = bestMonthData?.monthName ?: ""
        val bestMonthCount = bestMonthData?.totalSelected ?: 0

        return YearSummaryStats(
            completedMonths = completedMonths,
            totalCategoriesSelected = totalCategoriesSelected,
            averagePerMonth = averagePerMonth,
            bestMonth = bestMonth,
            bestMonthCount = bestMonthCount,
            year = year.value
        )
    }

    // Обновляем метод loadStatistics для работы с уже загруженными категориями
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                val allCategories = categoryRepository.getAll()
                reloadStatistics(allCategories)
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

    private suspend fun loadMonthData(
        month: YearMonth,
        categoryIds: List<Int>
    ): List<DayStatistics> {
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

    private suspend fun loadWeekData(
        weekStart: LocalDate,
        categoryIds: List<Int>
    ): List<DayStatistics> {
        val weekData = mutableListOf<DayStatistics>()

        // Генерируем все дни недели (с понедельника по воскресенье)
        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val dateString = date.toString()
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val isDayCompleted = dayCompletionRepo.getDayCompletion(dateString)

            if (isDayCompleted) {
                val dailyProgress = dailyProgressRepo.getByDate(dateString)
                val progressMap = dailyProgress.associate { it.categoryId to it.value }
                val totalSelected = progressMap.values.count { it }

                val categorySelections = categoryIds.associateWith { categoryId ->
                    progressMap[categoryId] ?: false
                }

                weekData.add(
                    DayStatistics(
                        day = date.dayOfMonth,
                        date = dateString,
                        dayOfWeek = dayOfWeek,
                        totalSelected = totalSelected,
                        categorySelections = categorySelections
                    )
                )
            } else {
                val categorySelections = categoryIds.associateWith { false }
                weekData.add(
                    DayStatistics(
                        day = date.dayOfMonth,
                        date = dateString,
                        dayOfWeek = dayOfWeek,
                        totalSelected = 0,
                        categorySelections = categorySelections
                    )
                )
            }
        }

        return weekData
    }

    private fun findRelevantCategories(
        data: List<DayStatistics>,
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>
    ): Set<Int> {
        val relevantCategoryIds = mutableSetOf<Int>()

        allCategories
            .filter { it.isVisible }
            .forEach { relevantCategoryIds.add(it.id) }

        data.forEach { dayData ->
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

    private fun filterCategories(
        allCategories: List<com.example.lifeinpoints.data.category.CategoryEntity>,
        relevantCategoryIds: Set<Int>
    ): List<CategoryStats> {
        return allCategories
            .filter { category -> category.id in relevantCategoryIds }
            .map { category -> CategoryStats(category.id, category.name, category.isVisible) }
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

    private fun filterWeekDataForRelevantCategories(
        weekData: List<DayStatistics>,
        relevantCategoryIds: Set<Int>
    ): List<DayStatistics> {
        return weekData.map { dayData ->
            val filteredSelections = dayData.categorySelections
                .filterKeys { categoryId -> categoryId in relevantCategoryIds }

            val newTotalSelected = filteredSelections.values.count { it }

            dayData.copy(
                totalSelected = newTotalSelected,
                categorySelections = filteredSelections
            )
        }
    }

    private fun calculateMonthSummaryStats(
        monthData: List<DayStatistics>,
        month: YearMonth
    ): SummaryStats {
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

    private fun calculateWeekSummaryStats(
        weekData: List<DayStatistics>,
        weekStart: LocalDate
    ): WeekSummaryStats {
        val completedDays = weekData.count { it.totalSelected > 0 }
        val totalCategoriesSelected = weekData.sumOf { it.totalSelected }
        val averagePerDay = if (completedDays > 0)
            totalCategoriesSelected.toDouble() / completedDays
        else 0.0

        val bestDayData = weekData.maxByOrNull { it.totalSelected }
        val bestDay = bestDayData?.dayOfWeek ?: ""
        val bestDayCount = bestDayData?.totalSelected ?: 0

        // Форматируем диапазон недели
        val weekEnd = weekStart.plusDays(6)
        val weekRange = if (weekStart.month == weekEnd.month) {
            "${weekStart.dayOfMonth} - ${weekEnd.dayOfMonth} ${weekStart.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}"
        } else {
            "${weekStart.dayOfMonth} ${weekStart.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} - " +
                    "${weekEnd.dayOfMonth} ${weekEnd.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}"
        }

        return WeekSummaryStats(
            completedDays = completedDays,
            totalCategoriesSelected = totalCategoriesSelected,
            averagePerDay = averagePerDay,
            bestDay = bestDay,
            bestDayCount = bestDayCount,
            weekRange = weekRange
        )
    }

    // Методы для навигации по месяцам
    fun nextMonth() {
        _uiState.update {
            it.copy(
                currentMonth = it.currentMonth.plusMonths(1),
                viewType = ViewType.MONTH
            )
        }
        loadStatistics()
    }

    fun prevMonth() {
        _uiState.update {
            it.copy(
                currentMonth = it.currentMonth.minusMonths(1),
                viewType = ViewType.MONTH
            )
        }
        loadStatistics()
    }

    // Методы для навигации по неделям
    fun nextWeek() {
        _uiState.update {
            it.copy(
                currentWeekStart = it.currentWeekStart.plusWeeks(1),
                viewType = ViewType.WEEK
            )
        }
        loadStatistics()
    }

    // Методы для навигации по годам
    fun nextYear() {
        _uiState.update {
            it.copy(
                currentYear = Year.of(it.currentYear.value + 1),
                viewType = ViewType.YEAR
            )
        }
        loadStatistics()
    }

    fun prevYear() {
        _uiState.update {
            it.copy(
                currentYear = Year.of(it.currentYear.value - 1),
                viewType = ViewType.YEAR
            )
        }
        loadStatistics()
    }
    fun prevWeek() {
        _uiState.update {
            it.copy(
                currentWeekStart = it.currentWeekStart.minusWeeks(1),
                viewType = ViewType.WEEK
            )
        }
        loadStatistics()
    }

    fun toggleViewType() {
        _uiState.update {
            val newViewType = when (it.viewType) {
                ViewType.MONTH -> ViewType.WEEK
                ViewType.WEEK -> ViewType.YEAR
                ViewType.YEAR -> ViewType.MONTH
            }
            it.copy(viewType = newViewType)
        }
        loadStatistics()
    }
    fun forceRefresh() {
        _forceRefresh.value = _forceRefresh.value + 1
    }

    fun refreshStatistics() {
        _forceRefresh.value = _forceRefresh.value + 1
    }
}