package com.example.lifeinpoints.calendar.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.unit.dp
import java.time.LocalDate

enum class DayStatus { GREEN, RED, GRAY, NONE }

@Composable
fun CalendarDayCell(
    date: LocalDate?,                 // null = leading/trailing filler
    inCurrentMonth: Boolean, // dim numbers outside current month
    modifier: Modifier = Modifier,
    status: DayStatus = DayStatus.NONE,

) {
    val shape = RoundedCornerShape(10.dp)
    val outline = MaterialTheme.colorScheme.outlineVariant
    val textColor =
        if (inCurrentMonth) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant

    val fillColor = when (status) {
        DayStatus.GREEN -> MaterialTheme.colorScheme.tertiaryContainer
        DayStatus.RED   -> MaterialTheme.colorScheme.errorContainer
        DayStatus.GRAY  -> MaterialTheme.colorScheme.surfaceVariant
        DayStatus.NONE  -> null
    }

    val isToday = date == LocalDate.now()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .border(
                width = if (isToday) 2.dp else 1.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else outline,
                shape = shape
            )
            .background(MaterialTheme.colorScheme.surface, shape)
            .padding(6.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // Day number
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }

        if (date != null && fillColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .clip(shape)
                    .background(fillColor.copy(alpha = 0.35f), shape)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    if (status == DayStatus.GREEN) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
        )
    }
}










@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360)
fun Preview_CalendarDayCell_Row() {
    val today = LocalDate.now()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarDayCell(today.minusDays(1), inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
        CalendarDayCell(today,            inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
        CalendarDayCell(today.plusDays(1),inCurrentMonth = true, status = DayStatus.GREEN, modifier = Modifier.weight(1f))
        CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
        CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
        CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
        CalendarDayCell(null,             inCurrentMonth = false, status = DayStatus.NONE,  modifier = Modifier.weight(1f))
    }
}
