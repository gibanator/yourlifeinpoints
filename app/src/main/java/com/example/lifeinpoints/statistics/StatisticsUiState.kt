// com.example.lifeinpoints.statistics/StatisticsUiState.kt
package com.example.lifeinpoints.statistics

import java.time.YearMonth

enum class ViewType {
    MONTH,
    WEEK
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val monthData: List<DayStatistics> = emptyList(),
    val categories: List<CategoryStats> = emptyList(),
    val viewType: ViewType = ViewType.MONTH,
    val summary: SummaryStats = SummaryStats()
)

data class DayStatistics(
    val day: Int,
    val date: String, // Добавляем полную дату для недельного режима
    val totalSelected: Int,
    val categorySelections: Map<Int, Boolean>
)

data class CategoryStats(
    val id: Int,
    val name: String,
    val isVisible: Boolean = true // Добавляем флаг видимости
)

data class SummaryStats(
    val totalDays: Int = 0,
    val completedDays: Int = 0,
    val totalCategoriesSelected: Int = 0,
    val averagePerDay: Double = 0.0,
    val bestDay: Int = 0,
    val bestDayCount: Int = 0
)