package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeinpoints.calendar.CalendarUiState
import com.example.lifeinpoints.calendar.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    vm: CalendarViewModel = viewModel()
) {
    val calendarUiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (calendarUiState.mode == CalendarUiState.Mode.MONTH)
                            calendarUiState.month.format(DateTimeFormatter.ofPattern("LLLL yyyy"))
                        else calendarUiState.month.year.toString()
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

}