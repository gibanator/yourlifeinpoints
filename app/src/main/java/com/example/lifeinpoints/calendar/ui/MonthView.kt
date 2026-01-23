package com.example.lifeinpoints.calendar.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.calendar.DayInMonth
import com.example.lifeinpoints.util.allDatesOfMonthView
import com.example.lifeinpoints.util.contains
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun MonthCalendar(
    monthDays: List<DayInMonth>,
    weeksCount: Int,
    onDateClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        repeat(weeksCount) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { weekday ->
                    val day = monthDays[week * 7 + weekday]
                    val fillColor = when (day.completionCategory) {
                        DayInMonth.CompletionCategory.COMPLETED -> Color(0xFF4CAF50)
                        DayInMonth.CompletionCategory.PARTIAL -> MaterialTheme.colorScheme.errorContainer
                        DayInMonth.CompletionCategory.NONE -> Color(0xFFFF9800)
                        DayInMonth.CompletionCategory.FUTURE -> Color.LightGray
                    }
                    Box(Modifier.weight(1f)
                        .aspectRatio(1f)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = if (day.isToday) 2.dp else 1.dp,
                            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(4.dp)
                        .clickable(enabled = day.isInCurrentMonth) {onDateClick(day.date.toString())}
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    color = if (day.isInCurrentMonth) fillColor else fillColor.copy(alpha = 0.3f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    month: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous"
            )
        }

        Text(
            text = month.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNext) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }
    }
}




@Composable
fun CalendarDayCell(
    date: LocalDate?,
    inCurrentMonth: Boolean,
    modifier: Modifier = Modifier,
    dayStatus: DayInMonth.CompletionCategory = DayInMonth.CompletionCategory.NONE,
    isToday: Boolean
) {
    val shape = RoundedCornerShape(10.dp)
    val outline = MaterialTheme.colorScheme.outlineVariant
    val textColor =
        if (inCurrentMonth) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant

    val fillColor = when (dayStatus) {
        DayInMonth.CompletionCategory.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
        DayInMonth.CompletionCategory.PARTIAL -> MaterialTheme.colorScheme.errorContainer
        DayInMonth.CompletionCategory.NONE -> MaterialTheme.colorScheme.surfaceVariant
        DayInMonth.CompletionCategory.FUTURE -> MaterialTheme.colorScheme.outline
    }

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
                    if (dayStatus == DayInMonth.CompletionCategory.COMPLETED)
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
        )
    }
}



@SuppressLint("ViewModelConstructorInComposable")
@Composable
@Preview(showBackground = true)
fun Calendar_Preview() {
    val dates = allDatesOfMonthView(YearMonth.now())
        .map { date ->
            DayInMonth(
                isToday = date == LocalDate.now(),
                isInCurrentMonth = YearMonth.now().contains(date),
                completionCategory = DayInMonth.CompletionCategory.COMPLETED,
                isFuture = true,
                date = date
            )
        }
    MonthCalendar(
        dates,
        weeksCount = 5,
        onDateClick = {}
    )
}






//@Composable
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 360)
//fun Preview_CalendarDayCell_Row() {
//    val today = LocalDate.now()
//    Row(
//        Modifier
//            .fillMaxWidth()
//            .padding(12.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        CalendarDayCell(today.minusDays(1), inCurrentMonth = true, dayStatus = DayInMonth.CompletionCategory.COMPLETED, modifier = Modifier.weight(1f))
//        CalendarDayCell(today,            inCurrentMonth = true, dayStatus = DayInMonth.CompletionCategory.COMPLETED, modifier = Modifier.weight(1f))
//        CalendarDayCell(today.plusDays(1),inCurrentMonth = true, dayStatus = DayInMonth.CompletionCategory.NONE, modifier = Modifier.weight(1f))
//        CalendarDayCell(null,             inCurrentMonth = false,  modifier = Modifier.weight(1f))
//        CalendarDayCell(null,             inCurrentMonth = false, modifier = Modifier.weight(1f))
//        CalendarDayCell(null,             inCurrentMonth = false, modifier = Modifier.weight(1f))
//        CalendarDayCell(null,             inCurrentMonth = false, modifier = Modifier.weight(1f))
//    }
//}
