@file:Suppress("DEPRECATION")

package com.example.lifeinpoints.statistics

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
//import com.example.lifeinpoints.core.ui.theme.LocalThemeType
//import com.example.lifeinpoints.core.ui.theme.ThemeType
//import com.example.lifeinpoints.core.ui.theme.isStoneTheme
import com.example.lifeinpoints.statistics.ui.AdaptiveMonthSummaryStatsCard
import com.example.lifeinpoints.statistics.ui.AdaptiveWeekSummaryStatsCard
import com.example.lifeinpoints.statistics.ui.AdaptiveYearSummaryStatsCard
import com.example.lifeinpoints.statistics.ui.CategoryFilter
import com.example.lifeinpoints.statistics.ui.PieChart.PieChartWithLegend
import com.example.lifeinpoints.statistics.ui.calculateAdaptiveFontSize
import com.example.lifeinpoints.statistics.ui.chart.ChartType
import com.example.lifeinpoints.statistics.ui.chart.ChartWithLegend
import com.example.lifeinpoints.statistics.ui.chart.TimePeriod
import com.example.lifeinpoints.statistics.ui.table.AdaptiveSmartCenteredTable
import com.example.lifeinpoints.statistics.ui.table.AdaptiveYearTable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import java.time.format.DateTimeFormatter

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    //val isStone = LocalThemeType.current.isStoneTheme
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }


    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.statistics_page_title),
                        //fontWeight = FontWeight.Bold,
                        //fontSize = calculateAdaptiveFontSize(screenHeight, 0.022f)
                    )
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                    // no bottom padding
                )
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            // Селектор периода
            AdaptivePeriodSelector(
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
                screenHeight = screenHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = screenHeight * 0.02f /*vertical = screenHeight * 0.01f*/)
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
                        color = MaterialTheme.colorScheme.error,
                        fontSize = calculateAdaptiveFontSize(screenHeight, 0.018f)
                    )
                }
            } else {
                // HorizontalPager для переключения между таблицей, круговой диаграммой и графиком
                HorizontalPager(
                    count = 3,
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
                                        AdaptiveSmartCenteredTable(
                                            data = uiState.monthData,
                                            categories = uiState.categories,
                                            isWeekMode = false,
                                            isYearMode = false,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                                    ViewType.WEEK -> {
                                        AdaptiveSmartCenteredTable(
                                            data = uiState.weekData,
                                            categories = uiState.categories,
                                            isWeekMode = true,
                                            isYearMode = false,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                                    ViewType.YEAR -> {
                                        AdaptiveYearTable(
                                            yearData = uiState.yearData,
                                            categories = uiState.categories,
                                            modifier = Modifier
                                                .weight(1f)
                                        )
                                    }
                                }

                                //Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                                // Сводная статистика
                                when (uiState.viewType) {
                                    ViewType.MONTH -> {
                                        AdaptiveMonthSummaryStatsCard(
                                            summary = uiState.monthSummary,
                                            currentMonth = uiState.currentMonth,
                                            screenHeight = screenHeight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(screenHeight * 0.02f)
                                        )
                                    }
                                    ViewType.WEEK -> {
                                        AdaptiveWeekSummaryStatsCard(
                                            summary = uiState.weekSummary,
                                            screenHeight = screenHeight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(screenHeight * 0.02f)
                                        )
                                    }
                                    ViewType.YEAR -> {
                                        AdaptiveYearSummaryStatsCard(
                                            summary = uiState.yearSummary,
                                            screenHeight = screenHeight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(screenHeight * 0.02f)
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Вторая страница: круговая диаграмма
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                PieChartWithLegend(
                                    data = uiState.pieChartData,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(screenHeight * 0.02f),
                                    title = when (uiState.viewType) {
                                        ViewType.MONTH -> stringResource(R.string.piechart_title_month)
                                        ViewType.WEEK -> stringResource(R.string.piechart_title_week)
                                        ViewType.YEAR -> stringResource(R.string.piechart_title_year)
                                    },
                                    innerRadiusRatio = 0.6f,
                                    showLegend = true,
                                    cardElevation = 2,
                                    cornerRadius = 12
                                )
                            }
                        }
                        // В StatisticsScreen.kt, на странице 2 (графики):
                        2 -> {
                            // Third page: time series chart
                            val configuration = LocalConfiguration.current
                            val screenWidth = configuration.screenWidthDp.dp
                            val screenHeight = configuration.screenHeightDp.dp

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Top
                            ) {
                                val dataToShow = if (uiState.selectedCategoryIds.isNotEmpty()) {
                                    uiState.filteredTimeSeriesData
                                } else {
                                    emptyList()
                                }

                                if (dataToShow.isNotEmpty()) {
                                    ChartWithLegend(
                                        timeSeriesData = dataToShow,
                                        title = when (uiState.viewType) {
                                            ViewType.MONTH -> stringResource(R.string.piechart_title_month)
                                            ViewType.WEEK -> stringResource(R.string.piechart_title_week)
                                            ViewType.YEAR -> stringResource(R.string.piechart_title_year)
                                        },
                                        chartType = ChartType.VERTICAL_BAR,
                                        timePeriod = when (uiState.viewType) {
                                            ViewType.MONTH -> TimePeriod.DAY
                                            ViewType.WEEK -> TimePeriod.WEEK
                                            ViewType.YEAR -> TimePeriod.MONTH
                                        },
                                        showGrid = true,
                                        barSpacingRatio = 0.2f,
                                        //barCornerRadius = 4f,
                                        showDataLabels = true
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.6f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (uiState.selectedCategoryIds.isEmpty()) {
                                                stringResource(R.string.chart_category_selection_annotation)
                                            } else {
                                                stringResource(R.string.chart_no_data_annotation)
                                            },
                                            fontSize = calculateAdaptiveFontSize(screenHeight, 0.018f),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Category filter with 3-column grid
                                if (uiState.categories.isNotEmpty()) {
                                    CategoryFilter(
                                        categories = uiState.categories,
                                        selectedCategoryIds = uiState.selectedCategoryIds,
                                        onCategoryToggle = { categoryId ->
                                            viewModel.toggleCategorySelection(categoryId)
                                        },
                                        onSelectAll = { viewModel.selectAllCategories() },
                                        onDeselectAll = { viewModel.deselectAllCategories() },
                                        screenHeight = screenHeight,
                                        screenWidth = screenWidth,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.4f) // 40% для фильтра
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
                        //.padding(bottom = screenHeight * 0.02f)
                )
            }
        }
    }
}

@Composable
fun AdaptivePeriodSelector(
    uiState: StatisticsUiState,
    onPrevPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    onToggleViewType: () -> Unit,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy")

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Кнопка предыдущего периода
        IconButton(
            onClick = onPrevPeriod,
            modifier = Modifier.size(screenHeight * 0.05f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier.size(screenHeight * 0.03f)
            )
        }

        // Отображение текущего периода
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = screenHeight * 0.01f),
            contentAlignment = Alignment.Center
        ) {
            val (periodText, periodName) = when (uiState.viewType) {
                ViewType.MONTH -> uiState.currentMonth.format(monthFormatter) to
                        stringResource(R.string.month)

                ViewType.WEEK -> uiState.weekSummary.weekRange to
                        stringResource(R.string.week)

                ViewType.YEAR -> uiState.currentYear.toString() to
                        stringResource(R.string.year)
            }

            Text(
                text = "$periodText • $periodName",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.02f)
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleViewType() }
                    .padding(vertical = screenHeight * 0.01f, horizontal = screenHeight * 0.02f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(screenHeight * 0.01f)
            )
        }

        // Кнопка следующего периода
        IconButton(
            onClick = onNextPeriod,
            modifier = Modifier.size(screenHeight * 0.05f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier.size(screenHeight * 0.03f)
            )
        }
    }
}