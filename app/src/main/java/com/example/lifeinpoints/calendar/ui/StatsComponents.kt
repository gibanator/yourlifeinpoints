package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.R

@Composable
fun MonthStatsCard(
    stats: MonthStats,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val screenW = LocalWindowInfo.current.containerSize.width.dp
    val donut = (screenW * 0.12f).coerceIn(84.dp, 120.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutPercent(
                percent = stats.percent,
                modifier = Modifier.size(donut)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.successful_days),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stats.successfulDays}/${stats.totalDaysInMonth} (${stats.percent}%)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "${stringResource(R.string.streak_current)} ${stats.currentStreak} ${stringResource(R.string.days)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${stringResource(R.string.streak_best)} ${stats.bestStreak} ${stringResource(R.string.days)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DonutPercent(
    percent: Int,
    modifier: Modifier = Modifier
) {
    val p = (percent.coerceIn(0, 100)) / 100f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { p },
            color = Color.Green,
            strokeWidth = 10.dp,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}