// com/example/lifeinpoints/statistics/ui/chart/ChartWithLegend.kt
package com.example.lifeinpoints.statistics.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.statistics.ui.PieChart.PieChartItem
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.theme.LocalThemeType
import com.example.lifeinpoints.core.ui.theme.isStoneTheme
import com.example.lifeinpoints.util.pastelIfNeeded
import com.example.lifeinpoints.util.toPastel

@Composable
fun ChartWithLegend(
    modifier: Modifier = Modifier,
    data: List<PieChartItem> = emptyList(),
    timeSeriesData: List<TimeSeriesData>? = null,
    title: String? = null,
    chartType: ChartType = ChartType.VERTICAL_BAR,
    timePeriod: TimePeriod = TimePeriod.MONTH,
    showGrid: Boolean = true,
    barSpacingRatio: Float = 0.2f,
    barCornerRadius: Float = 4f,
    showDataLabels: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    val chartHeight = screenHeight * 0.35f
    val padding = screenWidth * 0.02f
    val legendSpacing = screenHeight * 0.02f

    val dayNamesShort = stringArrayResource(R.array.day_names_short)
    val monthNamesShort = stringArrayResource(R.array.month_names_short)

    val themeType = LocalThemeType.current
    val pastelEnabled = themeType.isStoneTheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        val isTimeSeries = timeSeriesData != null

        if ((!isTimeSeries && data.isEmpty()) || (isTimeSeries && timeSeriesData?.isEmpty() != false)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.piechart_nodata),
                    style = TextStyle(
                        fontSize = calculateResponsiveFontSize(screenHeight, 0.02f),
                        color = Color.Gray
                    )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = padding * 0.5f, vertical = padding * 0.5f)
                ) {
                    if (isTimeSeries && timeSeriesData != null) {
                        when (chartType) {
                            ChartType.VERTICAL_BAR -> drawVerticalTimeSeriesChart(
                                data = timeSeriesData,
                                timePeriod = timePeriod,
                                showGrid = showGrid,
                                barSpacingRatio = barSpacingRatio,
                                barCornerRadius = barCornerRadius,
                                showDataLabels = showDataLabels,
                                dayNamesShort = dayNamesShort,
                                monthNamesShort = monthNamesShort
                            )
                            ChartType.LINE -> drawLineChart(
                                data = timeSeriesData,
                                timePeriod = timePeriod,
                                showGrid = showGrid,
                                showDataLabels = showDataLabels,
                                dayNamesShort = dayNamesShort,
                                monthNamesShort = monthNamesShort,
                                isPastel = pastelEnabled
                            )
                            else -> drawVerticalTimeSeriesChart(
                                data = timeSeriesData,
                                timePeriod = timePeriod,
                                showGrid = showGrid,
                                barSpacingRatio = barSpacingRatio,
                                barCornerRadius = barCornerRadius,
                                showDataLabels = showDataLabels,
                                dayNamesShort = dayNamesShort,
                                monthNamesShort = monthNamesShort
                            )
                        }
                    } else {
                        when (chartType) {
                            ChartType.VERTICAL_BAR -> drawVerticalBarChart(
                                data = data,
                                showGrid = showGrid,
                                barSpacingRatio = barSpacingRatio,
                                barCornerRadius = barCornerRadius,
                                showDataLabels = showDataLabels,
                                isPastel = pastelEnabled
                            )
                            else -> drawVerticalBarChart(
                                data = data,
                                showGrid = showGrid,
                                barSpacingRatio = barSpacingRatio,
                                barCornerRadius = barCornerRadius,
                                showDataLabels = showDataLabels,
                                isPastel = pastelEnabled
                            )
                        }
                    }
                }
            }

            if (!isTimeSeries && data.isNotEmpty() && data.size <= 10) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = legendSpacing)
                ) {
                    Legend(
                        data = data,
                        screenHeight = screenHeight,
                        isPastel = pastelEnabled,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawVerticalTimeSeriesChart(
    data: List<TimeSeriesData>,
    timePeriod: TimePeriod,
    showGrid: Boolean,
    barSpacingRatio: Float,
    barCornerRadius: Float,
    showDataLabels: Boolean,
    dayNamesShort: Array<String>,
    monthNamesShort: Array<String>
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOfOrNull { it.value } ?: 0f
    val itemCount = data.size

    val leftPadding = 50f
    val rightPadding = 16f
    val topPadding = 30f
    val bottomPadding = 40f

    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding

    val totalSpacingWidth = chartWidth * barSpacingRatio
    val availableWidthForBars = chartWidth - totalSpacingWidth
    val barWidth = availableWidthForBars / itemCount
    val spacing = barSpacingRatio * barWidth

    if (showGrid) {
        val gridLineCount = if (maxValue < 10) 5 else 6
        repeat(gridLineCount) { i ->
            val y = topPadding + chartHeight * (1 - i.toFloat() / (gridLineCount - 1))

            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1f
            )

            val value = maxValue * i / (gridLineCount - 1)
            val formattedValue = if (maxValue < 10) "%.1f".format(value) else value.toInt().toString()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    formattedValue,
                    leftPadding - 8f,
                    y + 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }
    }

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding + chartHeight),
        end = Offset(leftPadding + chartWidth, topPadding + chartHeight),
        strokeWidth = 2f
    )

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding),
        end = Offset(leftPadding, topPadding + chartHeight),
        strokeWidth = 2f
    )

    data.forEachIndexed { index, item ->
        val x = leftPadding + spacing / 2 + index * (barWidth + spacing)
        val barHeight = if (maxValue > 0) chartHeight * (item.value / maxValue) else 0f
        val y = topPadding + chartHeight - barHeight


        drawRoundRect(
            color = item.color ?: Color.Blue,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
        )

        if (showDataLabels) {
            val valueText = if (item.value % 1 == 0f) item.value.toInt().toString()
            else "%.1f".format(item.value)

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    valueText,
                    x + barWidth / 2,
                    y - 8f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        val label = when (timePeriod) {
            TimePeriod.DAY -> (index + 1).toString()
            TimePeriod.WEEK -> dayNamesShort.getOrNull(index) ?: (index + 1).toString()
            TimePeriod.MONTH -> monthNamesShort.getOrNull(index) ?: (index + 1).toString()
            TimePeriod.YEAR -> (index + 1).toString()
        }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                label,
                x + barWidth / 2,
                topPadding + chartHeight + 20f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    data: List<TimeSeriesData>,
    timePeriod: TimePeriod,
    showGrid: Boolean,
    showDataLabels: Boolean,
    dayNamesShort: Array<String>,
    monthNamesShort: Array<String>,
    isPastel: Boolean
) {
    if (data.isEmpty() || data.size < 2) return

    val maxValue = data.maxOfOrNull { it.value } ?: 0f
    val itemCount = data.size

    val leftPadding = 50f
    val rightPadding = 16f
    val topPadding = 30f
    val bottomPadding = 40f

    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding

    if (showGrid) {
        val gridLineCount = if (maxValue < 10) 5 else 6
        repeat(gridLineCount) { i ->
            val y = topPadding + chartHeight * (1 - i.toFloat() / (gridLineCount - 1))

            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1f
            )

            val value = maxValue * i / (gridLineCount - 1)
            val formattedValue = if (maxValue < 10) "%.1f".format(value) else value.toInt().toString()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    formattedValue,
                    leftPadding - 8f,
                    y + 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }
    }

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding + chartHeight),
        end = Offset(leftPadding + chartWidth, topPadding + chartHeight),
        strokeWidth = 2f
    )

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding),
        end = Offset(leftPadding, topPadding + chartHeight),
        strokeWidth = 2f
    )

    val points = data.mapIndexed { index, item ->
        val x = leftPadding + (chartWidth * index / (itemCount - 1).coerceAtLeast(1))
        val y = if (maxValue > 0) {
            topPadding + chartHeight * (1 - item.value / maxValue)
        } else {
            topPadding + chartHeight
        }
        Offset(x, y)
    }

    for (i in 0 until points.size - 1) {

        val baseColor = data[i].color ?: Color.Blue
        val finalColor = if (isPastel) baseColor.toPastel() else baseColor

        drawLine(
            color = finalColor,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 3f
        )
    }

    points.forEachIndexed { index, point ->

        val baseColor = data[index].color ?: Color.Blue
        val finalColor = if (isPastel) baseColor.toPastel() else baseColor

        drawCircle(
            color = finalColor,
            radius = 5f,
            center = point
        )

        if (showDataLabels) {
            val valueText = if (data[index].value % 1 == 0f)
                data[index].value.toInt().toString()
            else
                "%.1f".format(data[index].value)

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    valueText,
                    point.x,
                    point.y - 12f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 22f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }

    data.forEachIndexed { index, item ->
        val x = leftPadding + (chartWidth * index / (itemCount - 1).coerceAtLeast(1))
        val label = when (timePeriod) {
            TimePeriod.DAY -> (index + 1).toString()
            TimePeriod.WEEK -> dayNamesShort.getOrNull(index) ?: (index + 1).toString()
            TimePeriod.MONTH -> monthNamesShort.getOrNull(index) ?: (index + 1).toString()
            TimePeriod.YEAR -> (index + 1).toString()
        }

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                label,
                x,
                topPadding + chartHeight + 20f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

private fun DrawScope.drawVerticalBarChart(
    data: List<PieChartItem>,
    showGrid: Boolean,
    barSpacingRatio: Float,
    barCornerRadius: Float,
    showDataLabels: Boolean,
    isPastel: Boolean
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 0f
    val itemCount = data.size

    val leftPadding = 40f
    val rightPadding = 16f
    val topPadding = 30f
    val bottomPadding = 40f

    val chartWidth = size.width - leftPadding - rightPadding
    val chartHeight = size.height - topPadding - bottomPadding

    val totalSpacingWidth = chartWidth * barSpacingRatio
    val availableWidthForBars = chartWidth - totalSpacingWidth
    val barWidth = availableWidthForBars / itemCount
    val spacing = barSpacingRatio * barWidth

    if (showGrid) {
        val gridLineCount = 5
        repeat(gridLineCount) { i ->
            val y = topPadding + chartHeight * (1 - i.toFloat() / (gridLineCount - 1))

            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1f
            )

            val value = maxValue * i / (gridLineCount - 1)
            val formattedValue = if (maxValue < 10) "%.1f".format(value) else value.toInt().toString()

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    formattedValue,
                    leftPadding - 8f,
                    y + 4f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                )
            }
        }
    }

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding + chartHeight),
        end = Offset(leftPadding + chartWidth, topPadding + chartHeight),
        strokeWidth = 2f
    )

    drawLine(
        color = Color.Black,
        start = Offset(leftPadding, topPadding),
        end = Offset(leftPadding, topPadding + chartHeight),
        strokeWidth = 2f
    )

    data.forEachIndexed { index, item ->
        val x = leftPadding + spacing / 2 + index * (barWidth + spacing)
        val barHeight = if (maxValue > 0) chartHeight * (item.value / maxValue) else 0f
        val y = topPadding + chartHeight - barHeight

        val baseColor = item.color
        val finalColor = if (isPastel) baseColor.toPastel() else baseColor

        drawRoundRect(
            color = finalColor,
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
        )

        if (showDataLabels) {
            val valueText = if (item.value % 1 == 0f) item.value.toInt().toString()
            else "%.1f".format(item.value)

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    valueText,
                    x + barWidth / 2,
                    y - 8f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        val label = item.label
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                label,
                x + barWidth / 2,
                topPadding + chartHeight + 20f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 26f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
private fun Legend(
    data: List<PieChartItem>,
    screenHeight: Dp,
    isPastel: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        data.forEach { item ->
            val finalColor = if (isPastel) item.color.toPastel() else item.color
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = screenHeight * 0.005f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(screenHeight * 0.02f)
                        .background(finalColor)
                )
                Text(
                    text = "${item.label}: ${if (item.value % 1 == 0f) item.value.toInt() else "%.1f".format(item.value)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = calculateResponsiveFontSize(screenHeight, 0.016f)
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun calculateResponsiveFontSize(screenHeight: Dp, ratio: Float): TextUnit {
    val density = LocalDensity.current
    return with(density) {
        (screenHeight * ratio).toSp()
    }
}

enum class ChartType {
    VERTICAL_BAR,   // Вертикальная столбчатая диаграмма
    HORIZONTAL_BAR, // Горизонтальная столбчатая диаграмма
    LINE            // Линейный график
}

enum class TimePeriod {
    DAY,    // По дням
    WEEK,   // По неделям
    MONTH,  // По месяцам
    YEAR    // По годам
}

data class TimeSeriesData(
    val label: String,
    val value: Float,
    val color: Color? = null
)