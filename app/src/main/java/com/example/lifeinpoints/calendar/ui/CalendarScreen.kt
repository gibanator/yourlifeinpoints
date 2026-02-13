package com.example.lifeinpoints.calendar.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeinpoints.R
import com.example.lifeinpoints.calendar.CalendarUiState
import com.example.lifeinpoints.calendar.CalendarViewModel
import com.example.lifeinpoints.core.ui.AppTopAppBar
import com.example.lifeinpoints.statistics.ui.calculateAdaptiveFontSize
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    vm: CalendarViewModel = hiltViewModel(),
    toCertainDate: (String?) -> Unit
) {
    val calendarUiState by vm.uiState.collectAsStateWithLifecycle()

    val monthUi by vm.monthUi.collectAsStateWithLifecycle()
    val yearMonths by vm.yearUi.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = {
                    Text(
                        if (calendarUiState.mode == CalendarUiState.Mode.MONTH)
                            calendarUiState.selectedMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
                        else
                            calendarUiState.yearCursor.year.toString()
                    )
                },
                // modifier = Modifier.heightIn(max = 56.dp), если нужно поменять размер
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
                )
        ) {
            val isInMonthMode = if (calendarUiState.mode == CalendarUiState.Mode.MONTH) true else false
            CalendarModeSwitchCard(
                mode = calendarUiState.mode,
                selectedMonth = calendarUiState.selectedMonth,
                year = calendarUiState.yearCursor.year,
                onToggle = {
                    if (isInMonthMode) vm.openYear()
                    else vm.openMonth()
                },
                onPrev = {
                    if (isInMonthMode) vm.prevMonth()
                    else vm.prevYear()
                },
                onNext = {
                    if (isInMonthMode) vm.nextMonth()
                    else vm.nextYear()
                },
                screenHeight = screenHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = screenHeight * 0.02f)
            )
            if (calendarUiState.mode == CalendarUiState.Mode.MONTH) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( bottom = 10.dp ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        MonthCalendar(
                            monthDays = monthUi.days,
                            weeksCount = monthUi.weeksCount,
                            onDateClick = toCertainDate
                        )
                    }
                    item {
                        val stats = computeMonthStats(monthUi.month, monthUi.days)
                        MonthStatsCard(stats = stats)
                    }
                }
            }
            else {
                YearCalendarView(
                    months = yearMonths,
                    onMonthClick = { ym -> vm.openMonth(ym) }
                )
            }
        }
    }

}

@Composable
private fun CalendarModeSwitchCard(
    mode: CalendarUiState.Mode,
    selectedMonth: YearMonth,
    year: Int,
    onToggle: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    val title = if (mode == CalendarUiState.Mode.MONTH) {
        selectedMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
            .replaceFirstChar { it.uppercase() }
    } else {
        year.toString()
    }
    val period_name = if (mode == CalendarUiState.Mode.MONTH) {
        stringResource(R.string.month)
    } else {
        stringResource(R.string.year)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPrev,
            modifier = Modifier.size(screenHeight * 0.05f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier.size(screenHeight * 0.03f)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = screenHeight * 0.01f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$title • $period_name",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = calculateAdaptiveFontSize(screenHeight, 0.02f)
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
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
            onClick = onNext,
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