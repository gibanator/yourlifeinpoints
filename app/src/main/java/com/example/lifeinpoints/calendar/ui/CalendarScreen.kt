package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeinpoints.calendar.CalendarUiState
import com.example.lifeinpoints.calendar.CalendarViewModel
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
//    LaunchedEffect(Unit) {
//        vm.initMonthlyView(YearMonth.of(2025, 9))
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (calendarUiState.mode == CalendarUiState.Mode.MONTH)
                            calendarUiState.selectedMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
                        else
                            calendarUiState.yearCursor.year.toString()
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (calendarUiState.mode == CalendarUiState.Mode.MONTH) vm.openYear()
                            else vm.openMonth()
                        }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (calendarUiState.mode == CalendarUiState.Mode.MONTH) {

                val stats = computeMonthStats(monthUi.month, monthUi.days)

                CalendarHeader(
                    month = calendarUiState.selectedMonth,
                    onPrev = vm::prevMonth,
                    onNext = vm::nextMonth
                )
                MonthCalendar (
                    monthDays = monthUi.days,
                    weeksCount = monthUi.weeksCount,
                    onDateClick = toCertainDate
                )
                Spacer(Modifier.height(16.dp))

                MonthStatsCard(stats = stats)
            }
            else {
                YearCalendarView(
                    year = calendarUiState.yearCursor.year,
                    months = yearMonths,

                    onPrevYear = vm::prevYear,
                    onNextYear = vm::nextYear,

                    onMonthClick = { ym -> vm.openMonth(ym) }
                )
            }
        }
    }

}