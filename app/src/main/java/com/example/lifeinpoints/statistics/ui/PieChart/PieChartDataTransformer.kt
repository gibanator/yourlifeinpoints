package com.example.lifeinpoints.statistics.ui.PieChart

import androidx.compose.ui.graphics.Color
import com.example.lifeinpoints.statistics.CategoryStats
import com.example.lifeinpoints.statistics.DayStatistics
import com.example.lifeinpoints.statistics.MonthStatistics

/**
 * Объект-преобразователь данных для круговой диаграммы (Pie Chart).
 * Содержит методы для преобразования статистических данных в формат,
 * подходящий для отображения в круговой диаграмме.
 */
object PieChartDataTransformer {

    /**
     * Преобразует данные за месяц в формат для круговой диаграммы.
     *
     * @param monthData Список дневной статистики за месяц
     * @param categories Список категорий для отображения
     * @return Список элементов PieChartItem, отсортированный по убыванию значения
     */
    fun transformMonthData(
        monthData: List<DayStatistics>,
        categories: List<CategoryStats>
    ): List<PieChartItem> {
        // Мапа для хранения суммы выбранных раз по каждой категории
        val categorySums = mutableMapOf<Int, Float>()

        // Инициализируем все категории нулевыми значениями
        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        // Суммируем выбранные категории по всем дням месяца
        monthData.forEach { dayData ->
            dayData.categorySelections.forEach { (categoryId, isSelected) ->
                if (isSelected) {
                    // Если категория выбрана в этот день, увеличиваем счетчик
                    categorySums[categoryId] = categorySums[categoryId]!! + 1
                }
            }
        }

        // Фильтруем категории с ненулевыми значениями и преобразуем в PieChartItem
        return categorySums
            .filter { (_, value) -> value > 0 } // Оставляем только категории с данными
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    fallbackName = category?.name ?: "",
                    systemKey = category?.nameKey,
                    isSystem = category?.isSystem ?: false, // Название категории или "Unknown"
                    value = value,                       // Количество выборов за месяц
                    color = getCategoryColor(categoryId) // Цвет для категории
                )
            }
            .sortedByDescending { it.value } // Сортируем по убыванию количества выборов
    }

    /**
     * Преобразует данные за неделю в формат для круговой диаграммы.
     *
     * @param weekData Список дневной статистики за неделю
     * @param categories Список категорий для отображения
     * @return Список элементов PieChartItem, отсортированный по убыванию значения
     */
    fun transformWeekData(
        weekData: List<DayStatistics>,
        categories: List<CategoryStats>
    ): List<PieChartItem> {
        val categorySums = mutableMapOf<Int, Float>()

        // Инициализируем все категории
        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        // Суммируем выбранные категории по всем дням недели
        weekData.forEach { dayData ->
            dayData.categorySelections.forEach { (categoryId, isSelected) ->
                if (isSelected) {
                    categorySums[categoryId] = categorySums[categoryId]!! + 1
                }
            }
        }

        // Фильтруем и преобразуем данные
        return categorySums
            .filter { (_, value) -> value > 0 }
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    fallbackName = category?.name ?: "",
                    systemKey = category?.nameKey,
                    isSystem = category?.isSystem ?: false,
                    value = value,
                    color = getCategoryColor(categoryId)
                )
            }
            .sortedByDescending { it.value }
    }

    /**
     * Преобразует данные за год в формат для круговой диаграммы.
     * В отличие от недельных и месячных данных, здесь используются предварительно
     * агрегированные суммы по месяцам.
     *
     * @param yearData Список месячной статистики за год
     * @param categories Список категорий для отображения
     * @return Список элементов PieChartItem, отсортированный по убыванию значения
     */
    fun transformYearData(
        yearData: List<MonthStatistics>,
        categories: List<CategoryStats>
    ): List<PieChartItem> {
        val categorySums = mutableMapOf<Int, Float>()

        // Инициализируем все категории
        categories.forEach { category ->
            categorySums[category.id] = 0f
        }

        // Суммируем по всем месяцам года
        yearData.forEach { monthData ->
            monthData.categorySums.forEach { (categoryId, sum) ->
                if (sum > 0) {
                    // Суммируем количество выборов категории за все месяцы
                    categorySums[categoryId] = categorySums[categoryId]!! + sum
                }
            }
        }

        // Фильтруем и преобразуем данные
        return categorySums
            .filter { (_, value) -> value > 0 }
            .map { (categoryId, value) ->
                val category = categories.find { it.id == categoryId }
                PieChartItem(
                    fallbackName = category?.name ?: "",
                    systemKey = category?.nameKey,
                    isSystem = category?.isSystem ?: false,
                    value = value,
                    color = getCategoryColor(categoryId)
                )
            }
            .sortedByDescending { it.value }
    }

    /**
     * Получает цвет для категории на основе её ID.
     * Использует циклическое распределение цветов из предопределенной палитры.
     *
     * @param categoryId ID категории
     * @return Color для отображения сегмента диаграммы
     */
    private fun getCategoryColor(categoryId: Int): Color {
        // Палитра цветов Material Design для разных категорий
        val colors = listOf(
            Color(0xFF4285F4), // Синий (Google Blue)
            Color(0xFFEA4335), // Красный (Google Red)
            Color(0xFFFBBC05), // Желтый (Google Yellow)
            Color(0xFF34A853), // Зеленый (Google Green)
            Color(0xFF8B5CF6), // Фиолетовый (Violet)
            Color(0xFFEC4899), // Розовый (Pink)
            Color(0xFF10B981), // Изумрудный (Emerald)
            Color(0xFFF59E0B), // Оранжевый (Orange)
            Color(0xFF6366F1), // Индиго (Indigo)
            Color(0xFF14B8A6), // Бирюзовый (Teal)
        )

        // Используем остаток от деления для циклического распределения цветов
        // Это гарантирует, что цвет будет последовательным для каждой категории
        return colors[categoryId % colors.size]
    }
}