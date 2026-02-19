package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val showClassBadge = levelState.playerClassKey != "NOVICE"

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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Верхняя строка: уровень и класс
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

                    Spacer(modifier = Modifier.width(8.dp))

                    // Иконка или бейдж класса
                    if (levelState.playerClassKey != "Новичок") {
                        Text(
                            text = stringResource(R.string.level_class_bullet, className),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Нераспределенные очки (если есть)
                if (levelState.unspentSkillPoints > 0) {
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

            // Прогресс бар
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Текст прогресса
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            R.string.xp_progress_label,
                            levelState.currentXp,
                            levelState.xpToNextLevel
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = stringResource(R.string.percent_label, percent),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Сам прогресс-бар
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            }

            // Нижняя строка: дополнительная информация
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total_xp_label, levelState.totalXp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Последовательные дни
                if (levelState.consecutiveDays > 0) {
                    Text(
                        text = stringResource(R.string.consecutive_days_label, levelState.consecutiveDays),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}