package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.calendar.DayInMonth
import com.example.lifeinpoints.util.pastelIfNeeded

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
                    }.pastelIfNeeded()

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
