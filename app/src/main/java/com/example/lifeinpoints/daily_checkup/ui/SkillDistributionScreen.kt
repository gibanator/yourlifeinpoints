package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.data.level.LevelConstants
import com.example.lifeinpoints.level.LevelUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDistributionScreen(
    levelState: LevelUiState,
    onClose: () -> Unit,
    onSkillUpdated: (String, Int) -> Unit,
    onResetSkills: () -> Unit
) {
    // Рассчитываем текущий класс на основе навыков
    val currentClass = calculateClass(levelState)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Распределение навыков",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Уровень ${levelState.currentLevel} • $currentClass",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }

            // Нераспределенные очки
            if (levelState.unspentSkillPoints > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Нераспределенные очки: ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = levelState.unspentSkillPoints.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Список навыков
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkillRow(
                    skillName = "Сила",
                    skillValue = levelState.strength,
                    onIncrease = { onSkillUpdated("strength", 1) },
                    onDecrease = { onSkillUpdated("strength", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.strength > 0
                )

                SkillRow(
                    skillName = "Ловкость",
                    skillValue = levelState.agility,
                    onIncrease = { onSkillUpdated("agility", 1) },
                    onDecrease = { onSkillUpdated("agility", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.agility > 0
                )

                SkillRow(
                    skillName = "Харизма",
                    skillValue = levelState.charisma,
                    onIncrease = { onSkillUpdated("charisma", 1) },
                    onDecrease = { onSkillUpdated("charisma", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.charisma > 0
                )

                SkillRow(
                    skillName = "Воля",
                    skillValue = levelState.will,
                    onIncrease = { onSkillUpdated("will", 1) },
                    onDecrease = { onSkillUpdated("will", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.will > 0
                )

                SkillRow(
                    skillName = "Интеллект",
                    skillValue = levelState.intelligence,
                    onIncrease = { onSkillUpdated("intelligence", 1) },
                    onDecrease = { onSkillUpdated("intelligence", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.intelligence > 0
                )

                SkillRow(
                    skillName = "Выживание",
                    skillValue = levelState.survival,
                    onIncrease = { onSkillUpdated("survival", 1) },
                    onDecrease = { onSkillUpdated("survival", -1) },
                    canIncrease = levelState.unspentSkillPoints > 0,
                    canDecrease = levelState.survival > 0
                )
            }

            // Таблица классов
            //ClassTable(currentClass = currentClass)

            // Кнопка сброса
            if (levelState.strength + levelState.agility + levelState.charisma +
                levelState.will + levelState.intelligence + levelState.survival > 0) {
                OutlinedButton(
                    onClick = onResetSkills,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Сбросить все очки")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SkillRow(
    skillName: String,
    skillValue: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    canIncrease: Boolean,
    canDecrease: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skillName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Текущее значение: $skillValue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    enabled = canDecrease,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Уменьшить",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = skillValue.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = onIncrease,
                    enabled = canIncrease,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Увеличить",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/*
@Composable
fun ClassTable(currentClass: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Классы и множители",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Заголовок таблицы
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Класс", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("С", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Л", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Х", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("В", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("И", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Вы", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }

                // Данные классов
                LevelConstants.CLASS_MULTIPLIERS.forEach { (className, multipliers) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (className == currentClass) "★ $className" else className,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (className == currentClass) FontWeight.Bold else FontWeight.Normal,
                            color = if (className == currentClass) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )

                        multipliers.forEach { multiplier ->
                            Text(
                                text = "×$multiplier",
                                style = MaterialTheme.typography.bodySmall,
                                color = when (multiplier) {
                                    3 -> MaterialTheme.colorScheme.primary
                                    2 -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Ваш текущий класс: $currentClass",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

 */

private fun calculateClass(levelState: LevelUiState): String {
    val skills = listOf(
        levelState.strength,
        levelState.agility,
        levelState.charisma,
        levelState.will,
        levelState.intelligence,
        levelState.survival
    )

    var bestClass = "Новичок"
    var maxScore = 0

    LevelConstants.CLASS_MULTIPLIERS.forEach { (className, multipliers) ->
        val score = skills.zip(multipliers).sumOf { (skill, multiplier) ->
            skill * multiplier
        }
        if (score > maxScore) {
            maxScore = score
            bestClass = className
        }
    }

    return bestClass
}