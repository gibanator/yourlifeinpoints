package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.R
import com.example.lifeinpoints.level.LevelUiState
import kotlin.math.roundToInt

@Composable
fun XpProgressBar(
    levelState: LevelUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (levelState.xpToNextLevel > 0) {
        levelState.currentXp.toFloat() / levelState.xpToNextLevel
    } else {
        0f
    }
    val percent = (progress * 100).roundToInt()

    val className = stringResource(classLabelRes(levelState.playerClassKey))
    //val showClassBadge = levelState.playerClassKey != "NOVICE"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Одна строка: уровень + класс (+ бейдж очков) слева, процент справа
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.level_label, levelState.currentLevel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Класс игрока
                    if (levelState.playerClassKey != "NOVICE") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.level_class_bullet, className),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Нераспределенные очки (если есть)
                    if (levelState.unspentSkillPoints > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = pluralStringResource(
                                    id = R.plurals.unspent_points_badge,
                                    count = levelState.unspentSkillPoints,
                                    levelState.unspentSkillPoints
                                ),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.percent_label, percent),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Тонкий прогресс-бар
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}