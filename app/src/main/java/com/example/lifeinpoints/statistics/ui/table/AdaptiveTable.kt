package com.example.lifeinpoints.statistics.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeinpoints.statistics.CategoryStats
import com.example.lifeinpoints.statistics.DayStatistics
import com.example.lifeinpoints.statistics.MonthStatistics
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.category.categoryDisplayName

@Composable
fun AdaptiveSmartCenteredTable(
    data: List<DayStatistics>,
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    modifier: Modifier = Modifier,
    isYearMode: Boolean = false
) {
    //val configuration = LocalConfiguration.current
    val screenHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val totalCells = 2 + categories.size

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(screenHeight * 0.01f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(screenHeight * 0.02f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок таблицы
                AdaptiveTableHeaderRow(
                    categories = categories,
                    isWeekMode = isWeekMode,
                    isYearMode = isYearMode,
                    totalCells = totalCells,
                    screenHeight = screenHeight
                )

                Box(modifier = Modifier.height(screenHeight * 0.01f))

                // Тело таблицы
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(data) { dayData ->
                        AdaptiveTableRow(
                            dayData = dayData,
                            categories = categories,
                            isWeekMode = isWeekMode,
                            totalCells = totalCells,
                            screenHeight = screenHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdaptiveYearTable(
    yearData: List<MonthStatistics>,
    categories: List<CategoryStats>,
    modifier: Modifier = Modifier
) {
    //val configuration = LocalConfiguration.current
    val screenHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val totalCells = 2 + categories.size

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(screenHeight * 0.01f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(screenHeight * 0.02f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок таблицы для года
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
                ) {
                    // Колонка месяца
                    AdaptiveTableHeaderCell(
                        text = stringResource(R.string.month),
                        isVisible = true,
                        screenHeight = screenHeight,
                        modifier = Modifier
                            .height(screenHeight * 0.045f)
                            .weight(0.5f)
                    )

                    // Колонка суммы
                    AdaptiveTableHeaderCell(
                        text = stringResource(R.string.total_col_name),
                        screenHeight = screenHeight,
                        modifier = Modifier
                            .height(screenHeight * 0.045f)
                            .weight(0.6f)
                    )

                    // Колонки категорий
                    val categoryWeight = 0.4f
                    categories.forEach { category ->
                        AdaptiveTableHeaderCell(
                            text = categoryDisplayName(
                                category.name,
                                category.nameKey,
                                category.isVisible
                            ).take(3),
                            isVisible = category.isVisible,
                            screenHeight = screenHeight,
                            modifier = Modifier
                                .height(screenHeight * 0.045f)
                                .weight(categoryWeight)
                        )
                    }
                }

                Box(modifier = Modifier.height(screenHeight * 0.01f))

                // Тело таблицы для года
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(yearData) { monthData ->
                        AdaptiveYearTableRow(
                            monthData = monthData,
                            categories = categories,
                            totalCells = totalCells,
                            screenHeight = screenHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdaptiveTableHeaderRow(
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    isYearMode: Boolean = false,
    totalCells: Int,
    screenHeight: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Первая колонка: день недели или число месяца
        val firstColumnText = when {
            isYearMode -> stringResource(R.string.month)
            isWeekMode -> stringResource(R.string.day_col_name)
            else -> stringResource(R.string.day_col_name)
        }

        AdaptiveTableHeaderCell(
            text = firstColumnText,
            isVisible = true,
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.5f)
        )

        // Вторая колонка: сумма
        AdaptiveTableHeaderCell(
            text = stringResource(R.string.total_col_name),
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.6f)
        )

        // Колонки категорий
        val categoryWeight = 0.4f
        categories.forEach { category ->
            AdaptiveTableHeaderCell(
                text = categoryDisplayName(
                    category.name,
                    category.nameKey,
                    category.isVisible
                ).take(3),
                isVisible = category.isVisible,
                screenHeight = screenHeight,
                modifier = Modifier
                    .height(screenHeight * 0.045f)
                    .weight(categoryWeight)
            )
        }
    }
}

@Composable
private fun AdaptiveTableRow(
    dayData: DayStatistics,
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    totalCells: Int,
    screenHeight: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Первая колонка: день недели + число или просто число
        val dayText = if (isWeekMode) {
            "${dayData.dayOfWeek ?: ""} ${dayData.day}"
        } else {
            dayData.day.toString()
        }

        AdaptiveTableCell(
            text = dayText,
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.5f)
        )

        // Вторая колонка: сумма
        AdaptiveTableCell(
            text = dayData.totalSelected.toString(),
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.6f),
            backgroundColor = if (dayData.totalSelected > 0) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )

        // Колонки категорий
        val categoryWeight = 0.4f
        categories.forEach { category ->
            val isSelected = dayData.categorySelections[category.id] == true
            AdaptiveTableCell(
                text = if (isSelected) "✓" else "",
                screenHeight = screenHeight,
                modifier = Modifier
                    .height(screenHeight * 0.045f)
                    .weight(categoryWeight),
                backgroundColor = if (isSelected) {
                    if (category.isVisible) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    }
                } else {
                    MaterialTheme.colorScheme.surface
                },
                textColor = if (isSelected) {
                    if (category.isVisible) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                } else Color.Transparent
            )
        }
    }
}

@Composable
private fun AdaptiveYearTableRow(
    monthData: MonthStatistics,
    categories: List<CategoryStats>,
    totalCells: Int,
    screenHeight: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Колонка месяца
        AdaptiveTableCell(
            text = monthData.monthName,
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.5f)
        )

        // Колонка суммы
        AdaptiveTableCell(
            text = monthData.totalSelected.toString(),
            screenHeight = screenHeight,
            modifier = Modifier
                .height(screenHeight * 0.045f)
                .weight(0.6f),
            backgroundColor = if (monthData.totalSelected > 0) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )

        // Колонки категорий
        val categoryWeight = 0.4f
        categories.forEach { category ->
            val categorySum = monthData.categorySums[category.id] ?: 0
            val hasValue = categorySum > 0

            AdaptiveTableCell(
                text = if (hasValue) categorySum.toString() else "",
                screenHeight = screenHeight,
                modifier = Modifier
                    .height(screenHeight * 0.045f)
                    .weight(categoryWeight),
                backgroundColor = if (hasValue) {
                    val alpha = 0.2f + (categorySum.toFloat() / 31f * 0.8f).coerceAtMost(0.8f)
                    if (category.isVisible) {
                        MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                    } else {
                        MaterialTheme.colorScheme.secondary.copy(alpha = alpha)
                    }
                } else {
                    MaterialTheme.colorScheme.surface
                },
                textColor = if (hasValue) {
                    if (category.isVisible) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary
                } else Color.Transparent
            )
        }
    }
}

@Composable
fun AdaptiveTableHeaderCell(
    text: String,
    modifier: Modifier = Modifier,
    screenHeight: Dp,
    isVisible: Boolean = true

) {
    Box(
        modifier = modifier
            .border((screenHeight * 0.002f).coerceAtLeast(0.5.dp), MaterialTheme.colorScheme.outline)
            .background(
                if (isVisible) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding((screenHeight * 0.001f).coerceAtLeast(0.5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = calculateAdaptiveFontSize(screenHeight, 0.016f)
            )
            if (!isVisible) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.012f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AdaptiveTableCell(
    text: String,
    screenHeight: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .border((screenHeight * 0.002f).coerceAtLeast(0.5.dp), MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .background(backgroundColor)
            .padding((screenHeight * 0.001f).coerceAtLeast(0.5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = calculateAdaptiveFontSize(screenHeight, 0.016f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun calculateAdaptiveFontSize(screenHeight: Dp, percentage: Float): androidx.compose.ui.unit.TextUnit {
    val baseSize = screenHeight.value * percentage
    val minSize = 8f
    val maxSize = 14f
    return baseSize.coerceIn(minSize, maxSize).sp
}