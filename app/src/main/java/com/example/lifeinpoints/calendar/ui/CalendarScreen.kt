package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeinpoints.calendar.CalendarUiState
import com.example.lifeinpoints.calendar.CalendarViewModel
import com.example.lifeinpoints.core.ui.TopBarController
import com.example.lifeinpoints.core.ui.TopBarState
import java.time.LocalDate


@Composable
fun CalendarScreen(
    vm: CalendarViewModel = viewModel(),
    topBar: TopBarController
) {
    val calendarUiState by vm.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(calendarUiState.mode, calendarUiState.month) {
        val title = if (calendarUiState.mode == CalendarUiState.Mode.MONTH)
            calendarUiState.month.format(java.time.format.DateTimeFormatter.ofPattern("LLLL yyyy"))
        else calendarUiState.month.year.toString()

        topBar.set(
            TopBarState(
                title = title,
                actions = {
                    IconButton(onClick = {vm.switchMode()}) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select")
                    }
                }
            )
        )
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarDayCell(LocalDate.now().minusDays(1), inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
            CalendarDayCell(LocalDate.now(),            inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
            CalendarDayCell(LocalDate.now().plusDays(1),inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
            CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
            CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
            CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
            CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
        }
    }
}