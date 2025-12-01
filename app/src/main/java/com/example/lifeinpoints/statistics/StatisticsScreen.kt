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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
            CompactMonthSelector(
                currentMonth = uiState.currentMonth,
                viewType = uiState.viewType,
                onPrevMonth = { viewModel.prevMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onViewTypeToggle = { viewModel.toggleViewType() },
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
                // Таблица статистики с умным центрированием
                SmartCenteredTable(
                    monthData = uiState.monthData,
                    categories = uiState.categories,
                    viewType = uiState.viewType,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                SummaryStatsCard(
                    summary = uiState.summary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CompactMonthSelector(
    currentMonth: java.time.YearMonth,
    viewType: ViewType,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onViewTypeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("MMM yyyy")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPrevMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${currentMonth.format(formatter)} • ${viewType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewTypeToggle() }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            )
        }

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SummaryStatsCard(
    summary: SummaryStats,
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
                text = "Monthly Summary",
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

@Composable
fun SmartCenteredTable(
    monthData: List<DayStatistics>,
    categories: List<CategoryStats>,
    viewType: ViewType,
    modifier: Modifier = Modifier
) {
    // Рассчитываем общее количество ячеек
    val totalCells = 2 + categories.size // День + Сумма + Категории

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
                // Заголовок таблицы с учетом четности ячеек
                TableHeaderRow(
                    categories = categories,
                    viewType = viewType,
                    totalCells = totalCells,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Тело таблицы
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(monthData) { dayData ->
                        TableRow(
                            dayData = dayData,
                            categories = categories,
                            viewType = viewType,
                            totalCells = totalCells,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderRow(
    categories: List<CategoryStats>,
    viewType: ViewType,
    totalCells: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (totalCells % 2 == 0) {
            // Четное количество ячеек - центрируем между двумя центральными
            Arrangement.Center
        } else {
            // Нечетное количество ячеек - центрируем центральную ячейку
            Arrangement.Center
        }
    ) {
        // Распределяем ширину ячеек пропорционально их содержанию
        // День - самая узкая ячейка
        TableHeaderCell(
            text = when (viewType) {
                ViewType.MONTH -> "Day"
                ViewType.WEEK -> "Date"
            },
            isVisible = true,
            modifier = Modifier
                .height(28.dp)
                .weight(0.5f) // Самая узкая
        )

        // Сумма - немного шире дня
        TableHeaderCell(
            text = "Total",
            modifier = Modifier
                .height(28.dp)
                .weight(0.6f)
        )

        // Категории - самые узкие
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
    viewType: ViewType,
    totalCells: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (totalCells % 2 == 0) {
            Arrangement.Center
        } else {
            Arrangement.Center
        }
    ) {
        val dayText = when (viewType) {
            ViewType.MONTH -> dayData.day.toString()
            ViewType.WEEK -> dayData.date.substring(8..9)
        }

        // Ячейка дня - самая узкая
        TableCell(
            text = dayText,
            modifier = Modifier
                .height(28.dp)
                .weight(0.5f) // Самая узкая
        )

        // Ячейка суммы - немного шире
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

        // Ячейки категорий - самые узкие
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
                maxLines = 1,
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}