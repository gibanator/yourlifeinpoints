package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeinpoints.calendar.CalendarUiState
import com.example.lifeinpoints.calendar.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    vm: CalendarViewModel = viewModel(),
    toCertainDate: (LocalDate?) -> Unit
) {
    val calendarUiState by vm.uiState.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        vm.initMonthlyView(YearMonth.of(2025, 9))
//    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (calendarUiState.mode == CalendarUiState.Mode.MONTH)
                            calendarUiState.currentMonth.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
                        else calendarUiState.currentMonth.year.toString()
                    )
                },
                actions = {
                    IconButton(onClick = {vm.switchMode()}) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (calendarUiState.mode == CalendarUiState.Mode.MONTH) {
                CalendarHeader(
                    month = calendarUiState.currentMonth,
                    onPrev = { vm.prevMonth() },
                    onNext = { vm.nextMonth() }
                )
                MonthCalendar (
                    calendarUiState.days,
                    calendarUiState.weeksCount,
                    onDateClick = toCertainDate
                )
            }
        }
    }

}