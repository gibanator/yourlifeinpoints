// com.example.lifeinpoints.statistics/StatisticsUiState.kt
package com.example.lifeinpoints.statistics

import com.example.lifeinpoints.statistics.ui.PieChart.PieChartItem
import com.example.lifeinpoints.statistics.ui.chart.TimeSeriesData
import java.time.YearMonth
import java.time.LocalDate
import java.time.Year

enum class ViewType {
    MONTH,
    WEEK,
    YEAR
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val currentWeekStart: LocalDate = getStartOfCurrentWeek(),
    val currentYear: Year = Year.now(),
    val monthData: List<DayStatistics> = emptyList(),
    val weekData: List<DayStatistics> = emptyList(),
    val yearData: List<MonthStatistics> = emptyList(),
    val categories: List<CategoryStats> = emptyList(),
    val viewType: ViewType = ViewType.MONTH,
    val monthSummary: SummaryStats = SummaryStats(),
    val weekSummary: WeekSummaryStats = WeekSummaryStats(),
    val yearSummary: YearSummaryStats = YearSummaryStats(),
    val pieChartData: List<PieChartItem> = emptyList(),
    val timeSeriesData: List<TimeSeriesData> = emptyList(),
    val filteredTimeSeriesData: List<TimeSeriesData> = emptyList(), // Добавляем отфильтрованные данные
    val selectedCategoryIds: Set<Int> = emptySet(), // Выбранные категории
    val displayMode: DisplayMode = DisplayMode.TABLE
)

enum class DisplayMode {
    TABLE,
    CHART
}

data class DayStatistics(
    val day: Int,
    val date: String,
    val dayOfWeek: String? = null,
    val totalSelected: Int,
    val categorySelections: Map<Int, Boolean>
)

data class MonthStatistics(
    val monthNumber: Int,
    val monthName: String,
    val totalSelected: Int,
    val categorySums: Map<Int, Int>
)

data class CategoryStats(
    val id: Int,
    val name: String,
    val isVisible: Boolean = true
)

data class SummaryStats(
    val totalDays: Int = 0,
    val completedDays: Int = 0,
    val totalCategoriesSelected: Int = 0,
    val averagePerDay: Double = 0.0,
    val bestDay: Int = 0,
    val bestDayCount: Int = 0
)

data class WeekSummaryStats(
    val totalDays: Int = 7,
    val completedDays: Int = 0,
    val totalCategoriesSelected: Int = 0,
    val averagePerDay: Double = 0.0,
    val bestDay: String = "",
    val bestDayCount: Int = 0,
    val weekRange: String = ""
)

data class YearSummaryStats(
    val totalMonths: Int = 12,
    val completedMonths: Int = 0,
    val totalCategoriesSelected: Int = 0,
    val averagePerMonth: Double = 0.0,
    val bestMonth: String = "",
    val bestMonthCount: Int = 0,
    val year: Int = Year.now().value
)

fun getStartOfCurrentWeek(): LocalDate {
    val today = LocalDate.now()
    return today.with(java.time.DayOfWeek.MONDAY)
}