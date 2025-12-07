// com.example.lifeinpoints.statistics/StatisticsScreen.kt
package com.example.lifeinpoints.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.statistics.ui.PieChartWithLegend
import com.google.accompanist.pager.ExperimentalPagerApi
import java.time.format.DateTimeFormatter
import java.time.YearMonth
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Обновляем статистику при каждом появлении экрана
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }

    // Состояние для Pager
    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Statistics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Селектор периода
            PeriodSelector(
                uiState = uiState,
                onPrevPeriod = {
                    when (uiState.viewType) {
                        ViewType.MONTH -> viewModel.prevMonth()
                        ViewType.WEEK -> viewModel.prevWeek()
                        ViewType.YEAR -> viewModel.prevYear()
                    }
                },
                onNextPeriod = {
                    when (uiState.viewType) {
                        ViewType.MONTH -> viewModel.nextMonth()
                        ViewType.WEEK -> viewModel.nextWeek()
                        ViewType.YEAR -> viewModel.nextYear()
                    }
                },
                onToggleViewType = { viewModel.toggleViewType() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "Error loading statistics",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // HorizontalPager для переключения между таблицей и диаграммой
                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> {
                            // Первая страница: таблица
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Таблица статистики
                                when (uiState.viewType) {
                                    ViewType.MONTH -> {
                                        SmartCenteredTable(
                                            data = uiState.monthData,
                                            categories = uiState.categories,
                                            isWeekMode = false,
                                            isYearMode = false,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                    ViewType.WEEK -> {
                                        SmartCenteredTable(
                                            data = uiState.weekData,
                                            categories = uiState.categories,
                                            isWeekMode = true,
                                            isYearMode = false,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                    ViewType.YEAR -> {
                                        YearTable(
                                            yearData = uiState.yearData,
                                            categories = uiState.categories,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Сводная статистика
                                when (uiState.viewType) {
                                    ViewType.MONTH -> {
                                        MonthSummaryStatsCard(
                                            summary = uiState.monthSummary,
                                            currentMonth = uiState.currentMonth,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                    ViewType.WEEK -> {
                                        WeekSummaryStatsCard(
                                            summary = uiState.weekSummary,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                    ViewType.YEAR -> {
                                        YearSummaryStatsCard(
                                            summary = uiState.yearSummary,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Вторая страница: круговая диаграмма
                            if (uiState.pieChartData.isNotEmpty()) {
                                // Генерируем заголовок в зависимости от типа представления
                                val pieChartTitle = when (uiState.viewType) {
                                    ViewType.MONTH -> "Распределение по категориям за ${uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))}"
                                    ViewType.WEEK -> "Распределение по категориям за неделю"
                                    ViewType.YEAR -> "Распределение по категориям за ${uiState.currentYear}"
                                }

                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    PieChartWithLegend(
                                        data = uiState.pieChartData,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        title = pieChartTitle,
                                        innerRadiusRatio = 0.6f,
                                        showLegend = true,
                                        cardElevation = 4,
                                        cornerRadius = 16
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Нет данных для отображения диаграммы",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Индикатор страниц (точки внизу)
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PeriodSelector(
    uiState: StatisticsUiState,
    onPrevPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    onToggleViewType: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Кнопка предыдущего периода
        IconButton(
            onClick = onPrevPeriod,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier.size(24.dp)
            )
        }

        // Отображение текущего периода
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val periodText = when (uiState.viewType) {
                ViewType.MONTH -> uiState.currentMonth.format(monthFormatter)
                ViewType.WEEK -> uiState.weekSummary.weekRange
                ViewType.YEAR -> uiState.currentYear.toString()
            }

            Text(
                text = "$periodText • ${uiState.viewType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleViewType() }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            )
        }

        // Кнопка следующего периода
        IconButton(
            onClick = onNextPeriod,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SmartCenteredTable(
    data: List<DayStatistics>,
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    isYearMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val totalCells = 2 + categories.size

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок таблицы
                TableHeaderRow(
                    categories = categories,
                    isWeekMode = isWeekMode,
                    isYearMode = isYearMode,
                    totalCells = totalCells,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Тело таблицы
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(data) { dayData ->
                        TableRow(
                            dayData = dayData,
                            categories = categories,
                            isWeekMode = isWeekMode,
                            totalCells = totalCells,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Новая таблица для годового режима
@Composable
fun YearTable(
    yearData: List<MonthStatistics>,
    categories: List<CategoryStats>,
    modifier: Modifier = Modifier
) {
    val totalCells = 2 + categories.size

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок таблицы для года
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
                ) {
                    // Колонка месяца
                    TableHeaderCell(
                        text = "Month",
                        isVisible = true,
                        modifier = Modifier
                            .height(28.dp)
                            .weight(0.5f)
                    )

                    // Колонка суммы
                    TableHeaderCell(
                        text = "Total",
                        modifier = Modifier
                            .height(28.dp)
                            .weight(0.6f)
                    )

                    // Колонки категорий
                    val categoryWeight = 0.4f
                    categories.forEach { category ->
                        TableHeaderCell(
                            text = category.name.take(3),
                            isVisible = category.isVisible,
                            modifier = Modifier
                                .height(28.dp)
                                .weight(categoryWeight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Тело таблицы для года
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(yearData) { monthData ->
                        YearTableRow(
                            monthData = monthData,
                            categories = categories,
                            totalCells = totalCells,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Строка таблицы для года
@Composable
fun YearTableRow(
    monthData: MonthStatistics,
    categories: List<CategoryStats>,
    totalCells: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Колонка месяца
        TableCell(
            text = monthData.monthName,
            modifier = Modifier
                .height(28.dp)
                .weight(0.5f)
        )

        // Колонка суммы
        TableCell(
            text = monthData.totalSelected.toString(),
            modifier = Modifier
                .height(28.dp)
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

            TableCell(
                text = if (hasValue) categorySum.toString() else "",
                modifier = Modifier
                    .height(28.dp)
                    .weight(categoryWeight),
                backgroundColor = if (hasValue) {
                    // Разная интенсивность цвета в зависимости от значения
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
fun TableHeaderRow(
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    isYearMode: Boolean = false,
    totalCells: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Первая колонка: день недели или число месяца
        val firstColumnText = when {
            isYearMode -> "Month"
            isWeekMode -> "Day"
            else -> "Day"
        }

        TableHeaderCell(
            text = firstColumnText,
            isVisible = true,
            modifier = Modifier
                .height(28.dp)
                .weight(0.5f)
        )

        // Вторая колонка: сумма
        TableHeaderCell(
            text = "Total",
            modifier = Modifier
                .height(28.dp)
                .weight(0.6f)
        )

        // Колонки категорий
        val categoryWeight = 0.4f
        categories.forEach { category ->
            TableHeaderCell(
                text = category.name.take(3),
                isVisible = category.isVisible,
                modifier = Modifier
                    .height(28.dp)
                    .weight(categoryWeight)
            )
        }
    }
}

@Composable
fun TableRow(
    dayData: DayStatistics,
    categories: List<CategoryStats>,
    isWeekMode: Boolean,
    totalCells: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (totalCells % 2 == 0) Arrangement.Center else Arrangement.Center
    ) {
        // Первая колонка: день недели + число или просто число
        val dayText = if (isWeekMode) {
            "${dayData.dayOfWeek ?: ""}\n${dayData.day}"
        } else {
            dayData.day.toString()
        }

        TableCell(
            text = dayText,
            modifier = Modifier
                .height(28.dp)
                .weight(0.5f)
        )

        // Вторая колонка: сумма
        TableCell(
            text = dayData.totalSelected.toString(),
            modifier = Modifier
                .height(28.dp)
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
            TableCell(
                text = if (isSelected) "✓" else "",
                modifier = Modifier
                    .height(28.dp)
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
fun TableHeaderCell(
    text: String,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .background(
                if (isVisible) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(1.dp),
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
                fontSize = 10.sp
            )
            if (!isVisible) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .background(backgroundColor)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MonthSummaryStatsCard(
    summary: SummaryStats,
    currentMonth: YearMonth,
    modifier: Modifier = Modifier
) {
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${currentMonth.format(monthFormatter)} Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = "Days",
                    value = "${summary.completedDays}/${summary.totalDays}",
                    subtitle = "completed"
                )

                SummaryItem(
                    title = "Average",
                    value = "%.1f".format(summary.averagePerDay),
                    subtitle = "per day"
                )

                SummaryItem(
                    title = "Best Day",
                    value = if (summary.bestDay > 0) "#${summary.bestDay}" else "-",
                    subtitle = "${summary.bestDayCount} categories"
                )
            }
        }
    }
}

@Composable
fun WeekSummaryStatsCard(
    summary: WeekSummaryStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Week Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = "Days",
                    value = "${summary.completedDays}/${summary.totalDays}",
                    subtitle = "completed"
                )

                SummaryItem(
                    title = "Average",
                    value = "%.1f".format(summary.averagePerDay),
                    subtitle = "per day"
                )

                SummaryItem(
                    title = "Best Day",
                    value = summary.bestDay,
                    subtitle = "${summary.bestDayCount} categories"
                )
            }
        }
    }
}

// Новая карточка сводной статистики за год
@Composable
fun YearSummaryStatsCard(
    summary: YearSummaryStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${summary.year} Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    title = "Months",
                    value = "${summary.completedMonths}/${summary.totalMonths}",
                    subtitle = "completed"
                )

                SummaryItem(
                    title = "Average",
                    value = "%.1f".format(summary.averagePerMonth),
                    subtitle = "per month"
                )

                SummaryItem(
                    title = "Best Month",
                    value = summary.bestMonth,
                    subtitle = "${summary.bestMonthCount} categories"
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}