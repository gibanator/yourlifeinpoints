package com.example.lifeinpoints.statistics

//import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.category.CategoryEntity
import com.example.lifeinpoints.data.category.CategoryRepositoryNew
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressEntity
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressRepositoryNew
import com.example.lifeinpoints.data.daycompletion.DayCompletionRepository
import com.example.lifeinpoints.statistics.ui.PieChart.PieChartItem
import com.example.lifeinpoints.statistics.ui.chart.TimeSeriesColorKey
import com.example.lifeinpoints.statistics.ui.chart.TimeSeriesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
//import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.Year
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val dayCompletionRepo: DayCompletionRepository,
    private val dailyProgressRepo: DailyCategoryProgressRepositoryNew,
    private val categoryRepository: CategoryRepositoryNew
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _forceRefresh = MutableStateFlow(0)
    private var statisticsJob: Job? = null

    //private val dateFormatter = DateTimeFormatter.ofPattern("d")
    //private val weekDayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)
    //private val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)


    init {
        loadStatistics()
    }

    // Метод для переключения выбора категории
    fun toggleCategorySelection(categoryId: Int) {
        _uiState.update { currentState ->
            val newSelectedIds = if (currentState.selectedCategoryIds.contains(categoryId)) {
                currentState.selectedCategoryIds - categoryId
            } else {
                currentState.selectedCategoryIds + categoryId
            }

            // Пересчитываем временные данные с учетом выбранных категорий
            val recalculatedTimeSeriesData = when (currentState.viewType) {
                ViewType.MONTH -> prepareTimeSeriesDataForMonth(
                    currentState.monthData,
                    newSelectedIds
                )
                ViewType.WEEK -> prepareTimeSeriesDataForWeek(
                    currentState.weekData,
                    newSelectedIds
                )
                ViewType.YEAR -> prepareTimeSeriesDataForYear(
                    currentState.yearData,
                    newSelectedIds
                )
            }

            currentState.copy(
                selectedCategoryIds = newSelectedIds,
                filteredTimeSeriesData = recalculatedTimeSeriesData
            )
        }
    }

    // Метод для выбора всех категорий
    fun selectAllCategories() {
        _uiState.update { currentState ->
            val allCategoryIds = currentState.categories.map { it.id }.toSet()
            val recalculatedTimeSeriesData = when (currentState.viewType) {
                ViewType.MONTH -> prepareTimeSeriesDataForMonth(
                    currentState.monthData,
                    allCategoryIds
                )
                ViewType.WEEK -> prepareTimeSeriesDataForWeek(
                    currentState.weekData,
                    allCategoryIds
                )
                ViewType.YEAR -> prepareTimeSeriesDataForYear(
                    currentState.yearData,
                    allCategoryIds
                )
            }

            currentState.copy(
                selectedCategoryIds = allCategoryIds,
                filteredTimeSeriesData = recalculatedTimeSeriesData
            )
        }
    }

    // Метод для сброса выбора категорий
    fun deselectAllCategories() {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryIds = emptySet(),
                filteredTimeSeriesData = emptyList()
            )
        }
    }

    private data class Processed<T>(
        val filteredData: T,
        val categoriesToShow: List<CategoryStats>,
        val pieChartData: List<PieChartItem>,
        val initialSelectedIds: Set<Int>
    )

    private fun processDayData(
        rawData: List<DayStatistics>,
        allCategories: List<CategoryEntity>
    ): Processed<List<DayStatistics>> {
        val relevantIds = findRelevantCategories(rawData, allCategories)
        val categoriesToShow = filterCategories(allCategories, relevantIds)
        val filteredData = filterDayDataForRelevantCategories(rawData, relevantIds)
        return Processed(filteredData, categoriesToShow, preparePieChartDataForDays(filteredData, categoriesToShow), categoriesToShow.map { it.id }.toSet())
    }

    private fun processYearData(
        rawData: List<MonthStatistics>,
        allCategories: List<CategoryEntity>
    ): Processed<List<MonthStatistics>> {
        val relevantIds = findRelevantCategoriesForYear(rawData, allCategories)
        val categoriesToShow = filterCategories(allCategories, relevantIds)
        val filteredData = filterYearDataForRelevantCategories(rawData, relevantIds)
        return Processed(filteredData, categoriesToShow, preparePieChartDataForYear(filteredData, categoriesToShow), categoriesToShow.map { it.id }.toSet())
    }

    private fun reloadStatistics(
        allCategories: List<CategoryEntity>,
        completedDates: Set<String>,
        progressByDate: Map<String, List<DailyCategoryProgressEntity>>
    ) {
        try {
            val currentState = _uiState.value
            val categoryIds = allCategories.map { it.localId }

            when (currentState.viewType) {
                ViewType.MONTH -> {
                    val rawData = loadMonthData(
                        month = currentState.currentMonth,
                        categoryIds = categoryIds,
                        completedDates = completedDates,
                        progressByDate = progressByDate
                    )
                    val (filteredData, categoriesToShow, pieChartData, initialSelectedIds) = processDayData(rawData, allCategories)
                    val summary = calculateMonthSummaryStats(filteredData, currentState.currentMonth)
                    val timeSeriesData = prepareTimeSeriesDataForMonth(filteredData, initialSelectedIds)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            monthData = filteredData,
                            categories = categoriesToShow,
                            monthSummary = summary,
                            pieChartData = pieChartData,
                            timeSeriesData = timeSeriesData,
                            filteredTimeSeriesData = timeSeriesData,
                            selectedCategoryIds = initialSelectedIds
                        )
                    }
                }
                ViewType.WEEK -> {
                    val rawData = loadWeekData(
                        weekStart = currentState.currentWeekStart,
                        categoryIds = categoryIds,
                        completedDates = completedDates,
                        progressByDate = progressByDate
                    )
                    val (filteredData, categoriesToShow, pieChartData, initialSelectedIds) = processDayData(rawData, allCategories)
                    val summary = calculateWeekSummaryStats(filteredData, currentState.currentWeekStart)
                    val timeSeriesData = prepareTimeSeriesDataForWeek(filteredData, initialSelectedIds)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            weekData = filteredData,
                            categories = categoriesToShow,
                            weekSummary = summary,
                            pieChartData = pieChartData,
                            timeSeriesData = timeSeriesData,
                            filteredTimeSeriesData = timeSeriesData,
                            selectedCategoryIds = initialSelectedIds
                        )
                    }
                }
                ViewType.YEAR -> {
                    val rawData = loadYearData(
                        year = currentState.currentYear,
                        categoryIds = categoryIds,
                        completedDates = completedDates,
                        progressByDate = progressByDate
                    )
                    val (filteredData, categoriesToShow, pieChartData, initialSelectedIds) = processYearData(rawData, allCategories)
                    val summary = calculateYearSummaryStats(filteredData, currentState.currentYear)
                    val timeSeriesData = prepareTimeSeriesDataForYear(filteredData, initialSelectedIds)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            yearData = filteredData,
                            categories = categoriesToShow,
                            yearSummary = summary,
                            pieChartData = pieChartData,
                            timeSeriesData = timeSeriesData,
                            filteredTimeSeriesData = timeSeriesData,
                            selectedCategoryIds = initialSelectedIds
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

    // Подготовка временных данных для месяца с учетом выбранных категорий
    private fun prepareTimeSeriesDataForMonth(
        monthData: List<DayStatistics>,
        selectedCategoryIds: Set<Int>
    ): List<TimeSeriesData> {
        if (monthData.isEmpty() || selectedCategoryIds.isEmpty()) return emptyList()

        return monthData.mapIndexed { index, day ->
            // Считаем сумму только для выбранных категорий
            val sumForSelectedCategories = day.categorySelections
                .filterKeys { it in selectedCategoryIds }
                .values.count { it }

            TimeSeriesData(
                label = (index + 1).toString(),
                value = sumForSelectedCategories.toFloat(),
                colorKey = TimeSeriesColorKey.MONTH
            )
        }
    }

    // Подготовка временных данных для недели с учетом выбранных категорий
    private fun prepareTimeSeriesDataForWeek(
        weekData: List<DayStatistics>,
        selectedCategoryIds: Set<Int>
    ): List<TimeSeriesData> {
        if (weekData.isEmpty() || selectedCategoryIds.isEmpty()) return emptyList()

        return weekData.map { day ->
            val sumForSelectedCategories = day.categorySelections
                .filterKeys { it in selectedCategoryIds }
                .values.count { it }

            TimeSeriesData(
                label = day.dayOfWeek ?: day.day.toString(),
                value = sumForSelectedCategories.toFloat(),
                colorKey = TimeSeriesColorKey.WEEK
            )
        }
    }

    // Подготовка временных данных для года с учетом выбранных категорий
    private fun prepareTimeSeriesDataForYear(
        yearData: List<MonthStatistics>,
        selectedCategoryIds: Set<Int>
    ): List<TimeSeriesData> {
        if (yearData.isEmpty() || selectedCategoryIds.isEmpty()) return emptyList()

        return yearData.map { month ->
            val sumForSelectedCategories = month.categorySums
                .filterKeys { it in selectedCategoryIds }
                .values.sum()

            TimeSeriesData(
                label = month.monthName,
                value = sumForSelectedCategories.toFloat(),
                colorKey = TimeSeriesColorKey.YEAR
            )
        }
    }

    fun loadStatistics() {
        _uiState.update { it.copy(isLoading = true) }
        statisticsJob?.cancel()

        val dates = datesForCurrentView(_uiState.value)
        val dailyProgressFlow = observeProgressByDate(dates)

        statisticsJob = combine(
            categoryRepository.observeAll(),
            dayCompletionRepo.observeAllChanges(),
            dailyProgressFlow,
            _forceRefresh
        ) { allCategories, completedDays, progressByDate, _ ->
            StatisticsInput(
                allCategories = allCategories,
                completedDates = completedDays.map { it.date }.toSet(),
                progressByDate = progressByDate
            )
        }
            .catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
            .let { flow ->
                viewModelScope.launch {
                    flow.collect { input ->
                        reloadStatistics(
                            allCategories = input.allCategories,
                            completedDates = input.completedDates,
                            progressByDate = input.progressByDate
                        )
                    }
                }
            }
    }

    private data class StatisticsInput(
        val allCategories: List<CategoryEntity>,
        val completedDates: Set<String>,
        val progressByDate: Map<String, List<DailyCategoryProgressEntity>>
    )

    private fun datesForCurrentView(state: StatisticsUiState): List<String> {
        return when (state.viewType) {
            ViewType.MONTH -> (1..state.currentMonth.lengthOfMonth())
                .map { day -> state.currentMonth.atDay(day).toString() }
            ViewType.WEEK -> (0..6)
                .map { dayOffset -> state.currentWeekStart.plusDays(dayOffset.toLong()).toString() }
            ViewType.YEAR -> (1..12).flatMap { monthNumber ->
                val month = YearMonth.of(state.currentYear.value, monthNumber)
                (1..month.lengthOfMonth()).map { day -> month.atDay(day).toString() }
            }
        }
    }

    private fun observeProgressByDate(
        dates: List<String>
    ) = if (dates.isEmpty()) {
        flowOf(emptyMap())
    } else {
        combine(
            dates.map { date ->
                dailyProgressRepo.observeDay(date).map { rows -> date to rows }
            }
        ) { dateRows ->
            dateRows.toMap()
        }
    }

    private fun loadMonthData(
        month: YearMonth,
        categoryIds: List<Int>,
        completedDates: Set<String>,
        progressByDate: Map<String, List<DailyCategoryProgressEntity>>
    ): List<DayStatistics> {
        val daysInMonth = month.lengthOfMonth()
        val monthData = mutableListOf<DayStatistics>()

        for (day in 1..daysInMonth) {
            val date = month.atDay(day)
            val dateString = date.toString()
            val isDayCompleted = dateString in completedDates

            if (isDayCompleted) {
                val dailyProgress = progressByDate[dateString].orEmpty()
                val progressMap = dailyProgress.associate { it.categoryLocalId to it.value }
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

    private fun loadWeekData(
        weekStart: LocalDate,
        categoryIds: List<Int>,
        completedDates: Set<String>,
        progressByDate: Map<String, List<DailyCategoryProgressEntity>>
    ): List<DayStatistics> {
        val weekData = mutableListOf<DayStatistics>()

        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val dateString = date.toString()
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val isDayCompleted = dateString in completedDates

            if (isDayCompleted) {
                val dailyProgress = progressByDate[dateString].orEmpty()
                val progressMap = dailyProgress.associate { it.categoryLocalId to it.value }
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

    private fun loadYearData(
        year: Year,
        categoryIds: List<Int>,
        completedDates: Set<String>,
        progressByDate: Map<String, List<DailyCategoryProgressEntity>>
    ): List<MonthStatistics> {
        val yearData = mutableListOf<MonthStatistics>()

        for (monthNumber in 1..12) {
            val yearMonth = YearMonth.of(year.value, monthNumber)
            val monthName = yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            val categorySums = mutableMapOf<Int, Int>()
            categoryIds.forEach { categoryId ->
                categorySums[categoryId] = 0
            }

            var totalSelected = 0

            val daysInMonth = yearMonth.lengthOfMonth()
            for (day in 1..daysInMonth) {
                val date = yearMonth.atDay(day)
                val dateString = date.toString()
                val isDayCompleted = dateString in completedDates

                if (isDayCompleted) {
                    val dailyProgress = progressByDate[dateString].orEmpty()
                    val progressMap = dailyProgress.associate { it.categoryLocalId to it.value }

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

    private fun findRelevantCategories(
        data: List<DayStatistics>,
        allCategories: List<CategoryEntity>
    ): Set<Int> {
        val relevantCategoryIds = mutableSetOf<Int>()

        allCategories
            .filter { it.isVisible }
            .forEach { category -> relevantCategoryIds.add(category.localId) }

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

    private fun findRelevantCategoriesForYear(
        yearData: List<MonthStatistics>,
        allCategories: List<CategoryEntity>
    ): Set<Int> {
        val relevantCategoryIds = mutableSetOf<Int>()

        allCategories
            .filter { it.isVisible }
            .forEach { category -> relevantCategoryIds.add(category.localId) }

        yearData.forEach { monthData ->
            monthData.categorySums.forEach { (categoryId, sum) ->
                if (sum > 0) {
                    relevantCategoryIds.add(categoryId)
                }
            }
        }

        return relevantCategoryIds
    }

    private fun filterCategories(
        allCategories: List<CategoryEntity>,
        relevantCategoryIds: Set<Int>
    ): List<CategoryStats> {
        return allCategories
            .filter { category -> category.localId in relevantCategoryIds }
            .map { category -> CategoryStats(
                id = category.localId,
                name = category.name,
                nameKey = null,
                isVisible = category.isVisible,
                isSystem = category.isSystem
            ) }
    }

    private fun filterDayDataForRelevantCategories(
        data: List<DayStatistics>,
        relevantCategoryIds: Set<Int>
    ): List<DayStatistics> {
        return data.map { dayData ->
            val filteredSelections = dayData.categorySelections
                .filterKeys { it in relevantCategoryIds }
            dayData.copy(
                totalSelected = filteredSelections.values.count { it },
                categorySelections = filteredSelections
            )
        }
    }

    private fun filterYearDataForRelevantCategories(
        yearData: List<MonthStatistics>,
        relevantCategoryIds: Set<Int>
    ): List<MonthStatistics> {
        return yearData.map { monthData ->
            val filteredSums = monthData.categorySums
                .filterKeys { categoryId -> categoryId in relevantCategoryIds }

            val newTotalSelected = filteredSums.values.sum()

            monthData.copy(
                totalSelected = newTotalSelected,
                categorySums = filteredSums
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

    private fun calculateYearSummaryStats(
        yearData: List<MonthStatistics>,
        year: Year
    ): YearSummaryStats {
        val completedMonths = yearData.count { it.totalSelected > 0 }
        val totalCategoriesSelected = yearData.sumOf { it.totalSelected }
        val averagePerMonth = if (completedMonths > 0)
            totalCategoriesSelected.toDouble() / completedMonths
        else 0.0

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

    private fun preparePieChartDataForDays(
        data: List<DayStatistics>,
        categories: List<CategoryStats>
    ): List<PieChartItem> {
        if (categories.isEmpty()) return emptyList()

        val categoryCounts = mutableMapOf<Int, Int>()
        data.forEach { day ->
            if (day.totalSelected > 0) {
                day.categorySelections.forEach { (categoryId, isSelected) ->
                    if (isSelected) {
                        categoryCounts[categoryId] = categoryCounts.getOrDefault(categoryId, 0) + 1
                    }
                }
            }
        }

        return categories.mapIndexed { index, category ->
            PieChartItem(
                fallbackName = category.name,
                systemKey = category.nameKey,
                isSystem = category.isSystem,
                value = (categoryCounts[category.id] ?: 0).toFloat(),
                paletteIndex = index,
            )
        }.filter { it.value > 0 }
    }

    private fun preparePieChartDataForYear(
        yearData: List<MonthStatistics>,
        categories: List<CategoryStats>
    ): List<PieChartItem> {
        if (categories.isEmpty()) return emptyList()

        val categoryCounts = mutableMapOf<Int, Int>()
        yearData.forEach { month ->
            month.categorySums.forEach { (categoryId, sum) ->
                if (sum > 0) {
                    categoryCounts[categoryId] = categoryCounts.getOrDefault(categoryId, 0) + sum
                }
            }
        }

        return categories.mapIndexed { index, category ->
            val count = categoryCounts[category.id] ?: 0
            PieChartItem(
                fallbackName = category.name,
                systemKey = category.nameKey,
                isSystem = category.isSystem,
                value = count.toFloat(),
                paletteIndex = index
            )
        }.filter { it.value > 0 }
    }

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

    fun nextWeek() {
        _uiState.update {
            it.copy(
                currentWeekStart = it.currentWeekStart.plusWeeks(1),
                viewType = ViewType.WEEK
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
}
