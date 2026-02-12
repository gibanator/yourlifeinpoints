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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.calendar.DayInMonth
import com.example.lifeinpoints.calendar.MonthUi
import com.example.lifeinpoints.util.pastelIfNeeded
import java.time.YearMonth
import java.time.format.DateTimeFormatter


@Composable
fun YearCalendarView(
    months: List<MonthUi>, // 12 items
    onMonthClick: (YearMonth) -> Unit,  // switch to month mode
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
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
                    }.pastelIfNeeded()

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