// com/example/lifeinpoints/statistics/ui/PieChart.kt
package com.example.lifeinpoints.statistics.ui.PieChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.core.ui.category.categoryDisplayName
import com.example.lifeinpoints.core.ui.theme.LocalStatsPalette
import com.example.lifeinpoints.util.pastelIfNeeded
import kotlin.math.min

@Composable
private fun pieItemColor(item: PieChartItem): Color {
    val palette = LocalStatsPalette.current
    return palette.categories.getOrElse(item.paletteIndex) { Color.Gray }
}

/**
 * Композируемый компонент для отображения кольцевой диаграммы с легендой.
 * Автоматически адаптируется под количество данных и размер экрана.
 *
 * @param modifier Модификатор для настройки компоновки
 * @param data Список элементов для отображения на диаграмме
 * @param title Опциональный заголовок диаграммы
 * @param innerRadiusRatio Отношение внутреннего радиуса к внешнему (0-1), определяет толщину кольца
 * @param showLegend Флаг отображения легенды
 * @param cardElevation Тень карточки (если используется)
 * @param cornerRadius Радиус скругления углов
 * @param legendConfig Конфигурация отображения легенды
 */
@Composable
fun PieChartWithLegend(
    modifier: Modifier = Modifier,
    data: List<PieChartItem>,
    title: String? = null,
    innerRadiusRatio: Float = 0.5f,
    showLegend: Boolean = true,
    cardElevation: Int = 4,
    cornerRadius: Int = 16,
    legendConfig: LegendConfig = LegendConfig()
) {
    // Получаем параметры экрана для адаптивного дизайна
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    // Вычисляем размеры на основе процентов от экрана
    val chartHeight = screenHeight * 0.35f // 35% высоты экрана под диаграмму
    val padding = screenWidth * 0.04f // 4% от ширины экрана для отступов
    val legendSpacing = screenHeight * 0.02f // 2% высоты экрана между диаграммой и легендой

    val statsPalette = LocalStatsPalette.current

    val colorForIndex = remember(statsPalette) {
        { idx: Int -> statsPalette.categories.getOrElse(idx) { Color.Gray } }
    }

    // Суммируем все значения для расчета процентов
    val total = data.fold(0f) { acc, item -> acc + item.value }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок (опционально)
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = padding * 0.5f)
            )
        }

        if (total == 0f) {
            // Отображаем сообщение при отсутствии данных
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет данных для отображения",
                    style = TextStyle(
                        fontSize = calculateResponsiveFontSize(screenHeight, 0.02f),
                        color = Color.Gray
                    )
                )
            }
        } else {
            // Контейнер для диаграммы с адаптивной высотой
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val diameter = min(size.width, size.height) * 0.8f
                    val outerRadius = diameter / 2
                    val innerRadius = outerRadius * innerRadiusRatio
                    val center = Offset(size.width / 2, size.height / 2)

                    var startAngle = -90f // Начинаем с верхней точки (12 часов)

                    // Отрисовываем каждый сегмент диаграммы
                    data.forEach { item ->
                        val sweepAngle = (item.value / total) * 360f
                        val color = colorForIndex(item.paletteIndex)

                        drawPieSegment(
                            color = color,
                            center = center,
                            outerRadius = outerRadius,
                            innerRadius = innerRadius,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle
                        )

                        startAngle += sweepAngle
                    }
                }
            }

            // Легенда (если включена)
            if (showLegend) {
                Spacer(modifier = Modifier.height(legendSpacing))
                AdaptiveLegend(
                    data = data,
                    total = total,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    config = legendConfig,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Адаптивная легенда, которая автоматически выбирает оптимальный режим отображения
 * в зависимости от количества элементов и размера экрана.
 *
 * @param data Список элементов легенды
 * @param total Общая сумма всех значений (для расчета процентов)
 * @param screenWidth Ширина экрана
 * @param screenHeight Высота экрана
 * @param config Конфигурация легенды
 * @param modifier Модификатор для настройки компоновки
 */
@Composable
private fun AdaptiveLegend(
    data: List<PieChartItem>,
    total: Float,
    screenWidth: Dp,
    screenHeight: Dp,
    config: LegendConfig,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val itemCount = data.size

    // Определяем оптимальный тип легенды на основе количества элементов и ширины экрана
    val legendType = remember(itemCount, screenWidth, config) {
        calculateOptimalLegendType(
            itemCount = itemCount,
            screenWidth = screenWidth,
            minColumns = config.minColumns,
            maxColumns = config.maxColumns,
            maxItemsPerRow = config.maxItemsPerRow
        )
    }

    // Вычисляем размеры элементов на основе конфигурации и типа легенды
    val itemHeight = when (legendType) {
        LegendType.COLUMN -> screenHeight * config.itemHeightRatio // Полная высота для колонки
        LegendType.GRID -> screenHeight * config.itemHeightRatio * 0.8f // Уменьшаем для сетки
        LegendType.ROW -> screenHeight * config.itemHeightRatio * 0.6f // Еще меньше для строки
    }

    // Размер шрифта также адаптируется под тип легенды
    val fontSize = when (legendType) {
        LegendType.COLUMN -> calculateResponsiveFontSize(screenHeight, config.fontSizeRatio)
        LegendType.GRID -> calculateResponsiveFontSize(screenHeight, config.fontSizeRatio * 0.9f)
        LegendType.ROW -> calculateResponsiveFontSize(screenHeight, config.fontSizeRatio * 0.8f)
    }

    // Контейнер с динамической высотой в зависимости от типа легенды
    Box(
        modifier = modifier
            .height(when (legendType) {
                // Для колонки: высота элемента × количество элементов
                LegendType.COLUMN -> itemHeight * itemCount
                // Для сетки: высота элемента × количество строк (округляем вверх)
                LegendType.GRID -> itemHeight * (itemCount / 2 + if (itemCount % 2 != 0) 1 else 0)
                // Для строки: фиксированная высота одного элемента
                LegendType.ROW -> itemHeight
            })
    ) {
        when (legendType) {
            LegendType.COLUMN -> {
                // Вертикальная колонка с прокруткой
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f)
                ) {
                    data.forEach { item ->
                        LegendItem(
                            item = item,
                            percentage = if (total > 0) (item.value / total * 100).toInt() else 0,
                            itemHeight = itemHeight,
                            fontSize = fontSize,
                            showPercentage = config.showPercentage,
                            compactMode = false // Не компактный режим
                        )
                    }
                }
            }

            LegendType.GRID -> {
                // Сетка из 2 колонок
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.03f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f)
                ) {
                    items(data) { item ->
                        LegendItem(
                            item = item,
                            percentage = if (total > 0) (item.value / total * 100).toInt() else 0,
                            itemHeight = itemHeight,
                            fontSize = fontSize,
                            showPercentage = config.showPercentage,
                            compactMode = true // Компактный режим
                        )
                    }
                }
            }

            LegendType.ROW -> {
                // Горизонтальная строка с прокруткой
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.04f)
                ) {
                    items(data) { item ->
                        LegendItem(
                            item = item,
                            percentage = if (total > 0) (item.value / total * 100).toInt() else 0,
                            itemHeight = itemHeight,
                            fontSize = fontSize,
                            showPercentage = config.showPercentage,
                            compactMode = true // Компактный режим
                        )
                    }
                }
            }
        }
    }
}

/**
 * Элемент легенды, отображающий цветной индикатор, название категории и значение.
 *
 * @param item Данные элемента легенды
 * @param percentage Процентное значение элемента
 * @param itemHeight Высота элемента (адаптивная)
 * @param fontSize Размер шрифта (адаптивный)
 * @param showPercentage Флаг отображения процентного значения
 * @param compactMode Режим компактного отображения (меньше места между элементами)
 */
@Composable
private fun LegendItem(
    item: PieChartItem,
    percentage: Int,
    itemHeight: Dp,
    fontSize: TextUnit,
    showPercentage: Boolean = true,
    compactMode: Boolean = false
) {
    val palette = LocalStatsPalette.current
    val color = palette.categories.getOrElse(item.paletteIndex) { Color.Gray }

    Row(
        modifier = Modifier
            .fillMaxWidth(if (compactMode) 1f else 1f)
            .height(itemHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Цветовой индикатор (квадрат цвета категории)
        Box(
            modifier = Modifier
                .size(itemHeight * 0.6f) // 60% от высоты элемента
                .clip(RoundedCornerShape(itemHeight * 0.1f)) // Слегка скругленные углы
                .background(color)
        )

        Spacer(modifier = Modifier.width(itemHeight * 0.3f)) // Отступ 30% от высоты элемента

        // Колонка с названием категории и значением
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Название категории (максимум 2 строки)
            Text(
                text = categoryDisplayName(
                    item.fallbackName,
                    item.systemKey,
                    item.isSystem
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = fontSize,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis // Многоточие при переполнении
            )

            /*
            // Значение и процент (если включено)
            if (showPercentage) {
                Text(
                    text = "${item.value.toInt()} ${if (compactMode) "($percentage%)" else "($percentage%)"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = fontSize * 0.85f, // Немного меньше основного шрифта
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Вторичный цвет
                    maxLines = 1 // Одна строка для значений
                )
            }

             */
        }

        // Отдельный блок с процентом (только для некомпактного режима)
        if (!compactMode && showPercentage) {
            Spacer(modifier = Modifier.width(itemHeight * 0.3f))
            Text(
                text = "${item.value.toInt()} ${if (compactMode) "($percentage%)" else "($percentage%)"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = fontSize
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Определяет оптимальный тип отображения легенды на основе количества элементов
 * и размеров экрана.
 *
 * @param itemCount Количество элементов в легенде
 * @param screenWidth Ширина экрана
 * @param minColumns Минимальное количество колонок (из конфигурации)
 * @param maxColumns Максимальное количество колонок (из конфигурации)
 * @param maxItemsPerRow Максимальное количество элементов в строке (из конфигурации)
 * @return Оптимальный тип легенды
 */
private fun calculateOptimalLegendType(
    itemCount: Int,
    screenWidth: Dp,
    minColumns: Int = 1,
    maxColumns: Int = 3,
    maxItemsPerRow: Int = 6
): LegendType {
    return when {
        // Для очень большого количества элементов используем сетку
        itemCount > maxItemsPerRow * 2 -> LegendType.GRID

        // Для среднего количества используем колонку или сетку в зависимости от ширины экрана
        itemCount > 8 -> {
            // На широких экранах (планшеты) используем сетку, на узких - колонку
            if (screenWidth.value > 600) LegendType.GRID else LegendType.COLUMN
        }

        // Для небольшого количества (4 и меньше) используем горизонтальную линию
        itemCount <= 0 -> LegendType.ROW

        // По умолчанию используем колонку
        else -> LegendType.COLUMN
    }
}

/**
 * Отрисовывает сегмент кольцевой диаграммы.
 *
 * @param color Цвет сегмента
 * @param center Центр диаграммы
 * @param outerRadius Внешний радиус кольца
 * @param innerRadius Внутренний радиус кольца
 * @param startAngle Начальный угол сегмента (в градусах)
 * @param sweepAngle Угол развертки сегмента (в градусах)
 */

private fun DrawScope.drawPieSegment(
    color: Color,
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    startAngle: Float,
    sweepAngle: Float
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false, // Не закрашиваем центр (кольцо, а не сектор)
        topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
        size = Size(outerRadius * 2, outerRadius * 2),
        style = Stroke(width = outerRadius - innerRadius) // Толщина линии = разница радиусов
    )
}

/**
 * Вычисляет адаптивный размер шрифта на основе высоты экрана.
 *
 * @param screenHeight Высота экрана в Dp
 * @param ratio Процент от высоты экрана для размера шрифта (например, 0.02 = 2%)
 * @return Размер шрифта в TextUnit
 */
@Composable
private fun calculateResponsiveFontSize(screenHeight: Dp, ratio: Float): TextUnit {
    val density = LocalDensity.current
    return with(density) {
        (screenHeight * ratio).toSp() // Конвертируем в sp (масштабируемые пиксели)
    }
}

/**
 * Модель данных для элемента кольцевой диаграммы.
 *
 * @param label Название категории (отображается в легенде)
 * @param value Числовое значение категории
 * @param color Цвет сегмента на диаграмме
 */
data class PieChartItem(
    val fallbackName: String,
    val systemKey: String?,
    val isSystem: Boolean,

    val value: Float,
    val paletteIndex: Int
)

/**
 * Типы отображения легенды.
 */
enum class LegendType {
    COLUMN, // Вертикальный список (лучше для большого количества элементов)
    GRID,   // Сетка 2 колонки (компромисс между плотностью и читаемостью)
    ROW     // Горизонтальная линия (лучше для небольшого количества элементов)
}

/**
 * Конфигурация отображения легенды.
 *
 * @param minColumns Минимальное количество колонок в сетке
 * @param maxColumns Максимальное количество колонок в сетке
 * @param maxItemsPerRow Максимальное количество элементов в строке
 * @param showPercentage Флаг отображения процентных значений
 * @param itemHeightRatio Отношение высоты элемента к высоте экрана
 * @param fontSizeRatio Отношение размера шрифта к высоте экрана
 */
data class LegendConfig(
    val minColumns: Int = 1,
    val maxColumns: Int = 3,
    val maxItemsPerRow: Int = 6,
    val showPercentage: Boolean = true,
    val itemHeightRatio: Float = 0.06f, // 6% от высоты экрана
    val fontSizeRatio: Float = 0.018f   // 1.8% от высоты экрана
)