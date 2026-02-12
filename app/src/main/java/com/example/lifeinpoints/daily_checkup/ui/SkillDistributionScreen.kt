package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.R
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
    // Вместо использования levelState напрямую, создаем локальные копии
    var localUnspentPoints by remember { mutableIntStateOf(levelState.unspentSkillPoints) }
    var localStrength by remember { mutableIntStateOf(levelState.strength) }
    var localAgility by remember { mutableIntStateOf(levelState.agility) }
    var localCharisma by remember { mutableIntStateOf(levelState.charisma) }
    var localWill by remember { mutableIntStateOf(levelState.will) }
    var localIntelligence by remember { mutableIntStateOf(levelState.intelligence) }
    var localSurvival by remember { mutableIntStateOf(levelState.survival) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // При изменении levelState извне, обновляем локальные значения
    LaunchedEffect(levelState) {
        localUnspentPoints = levelState.unspentSkillPoints
        localStrength = levelState.strength
        localAgility = levelState.agility
        localCharisma = levelState.charisma
        localWill = levelState.will
        localIntelligence = levelState.intelligence
        localSurvival = levelState.survival
    }

    // Рассчитываем текущий класс на основе локальных значений
    val currentClassKey = calculateClass(
        strength = localStrength,
        agility = localAgility,
        charisma = localCharisma,
        will = localWill,
        intelligence = localIntelligence,
        survival = localSurvival
    )
    val currentClass = stringResource(classLabelRes(currentClassKey))

    // Сохраняем состояние скролла
    val listState = rememberLazyListState()

    // Вычисляем, есть ли распределенные очки
    val hasSpentPoints = localStrength + localAgility + localCharisma +
            localWill + localIntelligence + localSurvival > 0

    // Функция для обновления навыка
    fun updateLocalSkill(skillType: String, delta: Int) {
        when (skillType) {
            "strength" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localStrength += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("strength", delta)
                } else if (delta < 0 && localStrength >= -delta) {
                    localStrength += delta
                    localUnspentPoints -= delta // delta отрицательный, поэтому вычитаем отрицательное = прибавляем
                    onSkillUpdated("strength", delta)
                }
            }
            "agility" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localAgility += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("agility", delta)
                } else if (delta < 0 && localAgility >= -delta) {
                    localAgility += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("agility", delta)
                }
            }
            "charisma" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localCharisma += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("charisma", delta)
                } else if (delta < 0 && localCharisma >= -delta) {
                    localCharisma += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("charisma", delta)
                }
            }
            "will" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localWill += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("will", delta)
                } else if (delta < 0 && localWill >= -delta) {
                    localWill += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("will", delta)
                }
            }
            "intelligence" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localIntelligence += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("intelligence", delta)
                } else if (delta < 0 && localIntelligence >= -delta) {
                    localIntelligence += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("intelligence", delta)
                }
            }
            "survival" -> {
                if (delta > 0 && localUnspentPoints >= delta) {
                    localSurvival += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("survival", delta)
                } else if (delta < 0 && localSurvival >= -delta) {
                    localSurvival += delta
                    localUnspentPoints -= delta
                    onSkillUpdated("survival", delta)
                }
            }
        }
    }

    // Функция для сброса всех очков
    fun resetLocalSkills() {
        val totalSpent = localStrength + localAgility + localCharisma +
                localWill + localIntelligence + localSurvival
        localUnspentPoints += totalSpent
        localStrength = 0
        localAgility = 0
        localCharisma = 0
        localWill = 0
        localIntelligence = 0
        localSurvival = 0

        // Вызываем сброс в ViewModel
        onResetSkills()
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 16.dp
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Заголовок
            // Заголовок
            item {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // 1-я строка: заголовок + крестик
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.skill_distribution_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close))
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.skill_distribution_subtitle,
                                levelState.currentLevel,
                                currentClass
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (localUnspentPoints > 0) {
                            Spacer(Modifier.width(8.dp))
                            Badge(
                                modifier = Modifier.wrapContentWidth(),
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = pluralStringResource(
                                        R.plurals.unspent_points_badge, // типа: "%d очко/очка/очков"
                                        localUnspentPoints,
                                        localUnspentPoints
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

//             Нераспределенные очки
//            if (localUnspentPoints > 0) {
//                item {
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = CardDefaults.cardColors(
//                            containerColor = MaterialTheme.colorScheme.primaryContainer,
//                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                        )
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            horizontalArrangement = Arrangement.Center,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = stringResource(R.string.unspent_points_label),
//                                style = MaterialTheme.typography.bodyLarge
//                            )
//                            Text(
//                                text = localUnspentPoints.toString(),
//                                style = MaterialTheme.typography.headlineMedium,
//                                fontWeight = FontWeight.Bold,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }

            // Список навыков
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkillRow(
                        skillName = stringResource(R.string.skill_strength),
                        skillValue = localStrength,
                        onIncrease = { updateLocalSkill("strength", 1) },
                        onDecrease = { updateLocalSkill("strength", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localStrength > 0
                    )

                    SkillRow(
                        skillName = stringResource(R.string.skill_agility),
                        skillValue = localAgility,
                        onIncrease = { updateLocalSkill("agility", 1) },
                        onDecrease = { updateLocalSkill("agility", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localAgility > 0
                    )

                    SkillRow(
                        skillName = stringResource(R.string.skill_charisma),
                        skillValue = localCharisma,
                        onIncrease = { updateLocalSkill("charisma", 1) },
                        onDecrease = { updateLocalSkill("charisma", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localCharisma > 0
                    )

                    SkillRow(
                        skillName = stringResource(R.string.skill_will),
                        skillValue = localWill,
                        onIncrease = { updateLocalSkill("will", 1) },
                        onDecrease = { updateLocalSkill("will", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localWill > 0
                    )

                    SkillRow(
                        skillName = stringResource(R.string.skill_intelligence),
                        skillValue = localIntelligence,
                        onIncrease = { updateLocalSkill("intelligence", 1) },
                        onDecrease = { updateLocalSkill("intelligence", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localIntelligence > 0
                    )

                    SkillRow(
                        skillName = stringResource(R.string.skill_survival),
                        skillValue = localSurvival,
                        onIncrease = { updateLocalSkill("survival", 1) },
                        onDecrease = { updateLocalSkill("survival", -1) },
                        canIncrease = localUnspentPoints > 0,
                        canDecrease = localSurvival > 0
                    )
                }
            }

            // Кнопка сброса
            if (hasSpentPoints) {
                item {
                    OutlinedButton(
                        onClick = { resetLocalSkills() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.reset_all_points))
                    }
                }
            }

            // Нижний отступ
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
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
                    text = stringResource(R.string.current_value, skillValue),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (canDecrease) {
                            onDecrease()
                        }
                    },
                    enabled = canDecrease,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = stringResource(R.string.cd_decrease),
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
                    onClick = {
                        if (canIncrease) {
                            onIncrease()
                        }
                    },
                    enabled = canIncrease,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_increase),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun calculateClass(
    strength: Int,
    agility: Int,
    charisma: Int,
    will: Int,
    intelligence: Int,
    survival: Int
): String {
    val skills = listOf(strength, agility, charisma, will, intelligence, survival)

    // Если все навыки равны 0, возвращаем "Новичок"
    if (skills.all { it == 0 }) {
        return "NOVICE"
    }

    var bestClass = "NOVICE"
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