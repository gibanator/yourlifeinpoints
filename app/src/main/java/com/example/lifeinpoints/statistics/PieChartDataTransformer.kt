// com.example.lifeinpoints.statistics/ui/PieChartDataTransformer.kt
package com.example.lifeinpoints.statistics.ui

import androidx.compose.ui.graphics.Color
import com.example.lifeinpoints.statistics.DayStatistics
import com.example.lifeinpoints.statistics.MonthStatistics
import com.example.lifeinpoints.statistics.ViewType

object PieChartDataTransformer {

    fun transformMonthData(
        monthData: List<DayStatistics>,
        categories: List<com.example.lifeinpoints.statistics.CategoryStats>
    ): List<PieChartItem> {
        val categorySums = mutableMapOf<Int, Float>()

        // Инициализируем все категории
        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        // Суммируем выбранные категории по всем дням месяца
        monthData.forEach { dayData ->
            dayData.categorySelections.forEach { (categoryId, isSelected) ->
                if (isSelected) {
                    categorySums[categoryId] = categorySums[categoryId]!! + 1
                }
            }
        }

        // Фильтруем категории с ненулевыми значениями
        return categorySums
            .filter { (_, value) -> value > 0 }
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    label = category?.name ?: "Unknown",
                    value = value,
                    color = getCategoryColor(categoryId)
                )
            }
            .sortedByDescending { it.value }
    }

    fun transformWeekData(
        weekData: List<DayStatistics>,
        categories: List<com.example.lifeinpoints.statistics.CategoryStats>
    ): List<PieChartItem> {
        val categorySums = mutableMapOf<Int, Float>()

        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        weekData.forEach { dayData ->
            dayData.categorySelections.forEach { (categoryId, isSelected) ->
                if (isSelected) {
                    categorySums[categoryId] = categorySums[categoryId]!! + 1
                }
            }
        }

        return categorySums
            .filter { (_, value) -> value > 0 }
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    label = category?.name ?: "Unknown",
                    value = value,
                    color = getCategoryColor(categoryId)
                )
            }
            .sortedByDescending { it.value }
    }

    fun transformYearData(
        yearData: List<MonthStatistics>,
        categories: List<com.example.lifeinpoints.statistics.CategoryStats>
    ): List<PieChartItem> {
        val categorySums = mutableMapOf<Int, Float>()

        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        // Суммируем по всем месяцам
        yearData.forEach { monthData ->
            monthData.categorySums.forEach { (categoryId, sum) ->
                if (sum > 0) {
                    categorySums[categoryId] = categorySums[categoryId]!! + sum
                }
            }
        }

        return categorySums
            .filter { (_, value) -> value > 0 }
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    label = category?.name ?: "Unknown",
                    value = value,
                    color = getCategoryColor(categoryId)
                )
            }
            .sortedByDescending { it.value }
    }

    private fun getCategoryColor(categoryId: Int): Color {
        // Используем палитру цветов для разных категорий
        val colors = listOf(
            Color(0xFF4285F4), // Синий
            Color(0xFFEA4335), // Красный
            Color(0xFFFBBC05), // Желтый
            Color(0xFF34A853), // Зеленый
            Color(0xFF8B5CF6), // Фиолетовый
            Color(0xFFEC4899), // Розовый
            Color(0xFF10B981), // Изумрудный
            Color(0xFFF59E0B), // Оранжевый
            Color(0xFF6366F1), // Индиго
            Color(0xFF14B8A6), // Бирюзовый
        )

        return colors[categoryId % colors.size]
    }
}