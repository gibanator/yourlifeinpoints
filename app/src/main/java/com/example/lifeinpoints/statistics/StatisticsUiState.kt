// com.example.lifeinpoints.statistics/StatisticsUiState.kt
package com.example.lifeinpoints.statistics

import java.time.YearMonth
import java.time.LocalDate
import java.time.Year

enum class ViewType {
    MONTH,
    WEEK,
    YEAR  // Добавляем годовой режим
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMonth: YearMonth = YearMonth.now(),
    val currentWeekStart: LocalDate = getStartOfCurrentWeek(),
    val currentYear: Year = Year.now(),  // Добавляем текущий год
    val monthData: List<DayStatistics> = emptyList(),
    val weekData: List<DayStatistics> = emptyList(),
    val yearData: List<MonthStatistics> = emptyList(),  // Данные за год
    val categories: List<CategoryStats> = emptyList(),
    val viewType: ViewType = ViewType.MONTH,
    val monthSummary: SummaryStats = SummaryStats(),
    val weekSummary: WeekSummaryStats = WeekSummaryStats(),
    val yearSummary: YearSummaryStats = YearSummaryStats()  // Статистика за год
)

data class DayStatistics(
    val day: Int,
    val date: String,
    val dayOfWeek: String? = null,
    val totalSelected: Int,
    val categorySelections: Map<Int, Boolean>
)

// Новая структура для месячной статистики в годовом режиме
data class MonthStatistics(
    val monthNumber: Int, // 1-12
    val monthName: String, // "Jan", "Feb", etc.
    val totalSelected: Int, // Общая сумма выбранных категорий за месяц
    val categorySums: Map<Int, Int> // categoryId -> сумма выбранных раз за месяц
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

// Новая статистика для года
data class YearSummaryStats(
    val totalMonths: Int = 12,
    val completedMonths: Int = 0, // Месяцы, в которых был хотя бы один завершенный день
    val totalCategoriesSelected: Int = 0,
    val averagePerMonth: Double = 0.0,
    val bestMonth: String = "", // Название лучшего месяца
    val bestMonthCount: Int = 0,
    val year: Int = Year.now().value
)

// Вспомогательная функция для получения начала текущей недели (понедельник)
fun getStartOfCurrentWeek(): LocalDate {
    val today = LocalDate.now()
    return today.with(java.time.DayOfWeek.MONDAY)
}