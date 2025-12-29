package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.calendar.DayInMonth
import com.example.lifeinpoints.calendar.MonthUi
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@Composable
fun YearCalendarView(
    year: Int,
    months: List<MonthUi>, // 12 items
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit,
    onMonthClick: (YearMonth) -> Unit,  // switch to month mode
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CalendarYearHeader(
            year = year,
            onPrevYear = onPrevYear,
            onNextYear = onNextYear
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // classic 3x4
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(months) { m ->
                YearMonthMiniCard(
                    month = m.month,
                    days = m.days,
                    weeksCount = m.weeksCount,
                    onClick = { onMonthClick(m.month) }
                )
            }
        }
    }
}

@Composable
fun CalendarYearHeader(
    year: Int,
    onPrevYear: () -> Unit,
    onNextYear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevYear) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous year")
        }

        Text(
            text = year.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextYear) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next year")
        }
    }
}

@Composable
fun YearMonthMiniCard(
    month: YearMonth,
    days: List<DayInMonth>,
    weeksCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern("LLL")),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        CompactMonthCalendar(
            monthDays = days,
            weeksCount = weeksCount,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun CompactMonthCalendar(
    monthDays: List<DayInMonth>,
    weeksCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(weeksCount) { week ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { weekday ->
                    val day = monthDays[week * 7 + weekday]

                    val fillColor = when (day.completionCategory) {
                        DayInMonth.CompletionCategory.COMPLETED -> Color(0xFF4CAF50)
                        DayInMonth.CompletionCategory.PARTIAL -> MaterialTheme.colorScheme.errorContainer
                        DayInMonth.CompletionCategory.NONE -> Color(0xFFFF9800)
                        DayInMonth.CompletionCategory.FUTURE -> Color.LightGray
                    }

                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (day.isInCurrentMonth) fillColor else fillColor.copy(alpha = 0.25f)
                            )
                    )
                }
            }
        }
    }
}