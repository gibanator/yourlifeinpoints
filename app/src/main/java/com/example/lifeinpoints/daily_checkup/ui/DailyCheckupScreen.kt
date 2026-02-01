package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.level.LevelViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckupScreen(
    modifier: Modifier = Modifier,
    vm: DailyCheckupViewModel = hiltViewModel(),
    onNavigateToComments: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM yyyy") }

    // Получаем ViewModel для уровней и настроек
    val levelVm: LevelViewModel = hiltViewModel()
    val settingsVm: SettingsViewModel = hiltViewModel()

    // Состояния
    val levelState by levelVm.levelState.collectAsState()
    val gameModeEnabled by settingsVm.gameModeEnabled.collectAsState()

    // Состояние для открытия экрана прокачки
    var showSkillScreen by remember { mutableStateOf(false) }

    // Состояние для скролла всего экрана
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.selectedDate.format(formatter)) },
                actions = {
                    OutlinedButton(onClick = { vm.goToToday() }) {
                        Text("To today")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState) // Скролл всего экрана
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    //bottom = 16.dp // Добавили нижний отступ
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeekBarWithButtons(
                days = uiState.currentWeek,
                onDaySelected = vm::onDaySelected,
                toPrevWeek = vm::toPrevWeek,
                toNextWeek = vm::toNextWeek,
            )

            // Полоска XP (только если включен Game Mode)
            if (gameModeEnabled) {
                XpProgressBar(
                    levelState = levelState,
                    onClick = {
                        showSkillScreen = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Карточка с категориями - теперь без внутреннего скролла
            CategoryListCard(
                categories = uiState.orderedCategories,
                selectedCategories = uiState.selectedCategories,
                isDayEnded = uiState.isDayEnded,
                isMultiplierMode = uiState.isMultiplierMode,
                onCategoryClick = { id ->
                    vm.toggleCategory(id)
                },
                onToggleMultiplierMode = { vm.toggleMultiplierMode() },
                onAddComment = onNavigateToComments,
                modifier = Modifier.fillMaxWidth()
            )

            DayCompletionCard(
                isDayEnded = uiState.isDayEnded,
                onToggleDayEnded = {
                    vm.toggleDayEnded()
                    vm.saveProgress()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Диалог повышения уровня (если сработало событие)
        val levelUpEvent by vm.levelUpEvent.collectAsState()
        levelUpEvent?.let { newLevel ->
            LevelUpDialog(
                level = newLevel,
                unspentSkillPoints = levelState.unspentSkillPoints,
                onDismiss = { vm.levelUpEventConsumed() },
                onGoToSkills = {
                    vm.levelUpEventConsumed()
                    showSkillScreen = true
                }
            )
        }

        // Экран прокачки навыков
        if (showSkillScreen) {
            SkillDistributionScreen(
                levelState = levelState,
                onClose = { showSkillScreen = false },
                onSkillUpdated = { skillType, delta ->
                    levelVm.updateSkill(skillType, delta)
                },
                onResetSkills = { levelVm.resetSkills() }
            )
        }
    }
}

@Composable
fun LevelUpDialog(
    level: Int,
    unspentSkillPoints: Int,
    onDismiss: () -> Unit,
    onGoToSkills: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎉 Поздравляем!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Вы достигли уровня $level!",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Вы получили 5 очков навыков.",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (unspentSkillPoints > 0) {
                    Text(
                        text = "Всего нераспределенных очков: $unspentSkillPoints",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Позже")
                }

                Button(
                    onClick = {
                        onGoToSkills()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Распределить")
                }
            }
        }
    )
}

@Composable
fun WeekBarWithButtons(
    modifier: Modifier = Modifier,
    days: List<DayForWeekBar>,
    onDaySelected: (LocalDate) -> Unit,
    toPrevWeek: () -> Unit,
    toNextWeek: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = toPrevWeek) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous week")
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            if (day.isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { onDaySelected(day.date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (day.isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        IconButton(onClick = toNextWeek) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next week")
        }
    }
}

@Composable
fun CategoryListCard(
    categories: List<CategoryUi>,
    selectedCategories: Set<Int>,
    isDayEnded: Boolean,
    isMultiplierMode: Boolean,
    onCategoryClick: (Int) -> Unit,
    onToggleMultiplierMode: () -> Unit,
    onAddComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Список категорий - теперь без внутреннего скролла
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category.id in selectedCategories
                    CategoryRow(
                        category = category.name,
                        isSelected = isSelected,
                        isDayEnded = isDayEnded,
                        onCategoryClick = { onCategoryClick(category.id) }
                    )
                }
            }

            // Ряд с двумя кнопками
            ActionButtonsRow(
                selectedCount = selectedCategories.size,
                totalCount = if (isMultiplierMode) categories.size * 31 else categories.size,
                isDayEnded = isDayEnded,
                isMultiplierMode = isMultiplierMode,
                onToggleMultiplierMode = onToggleMultiplierMode,
                onAddComment = onAddComment,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ActionButtonsRow(
    selectedCount: Int,
    totalCount: Int,
    isDayEnded: Boolean,
    isMultiplierMode: Boolean,
    onToggleMultiplierMode: () -> Unit,
    onAddComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onAddComment),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Add/Edit comments",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onToggleMultiplierMode),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedCount > 0) {
                    if (isMultiplierMode) {
                        Color(0xFF7E6DF8)
                    } else {
                        Color(0xFF7E6DF8)
                    }
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "$selectedCount/$totalCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCount > 0) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: String,
    isSelected: Boolean,
    isDayEnded: Boolean,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberCard(
            number = if (isSelected) "1" else "0",
            modifier = Modifier
                .size(50.dp)
                .padding(end = 8.dp)
        )

        CategoryListItem(
            category = category,
            isSelected = isSelected,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 50.dp)
                .clickable(
                    enabled = !isDayEnded,
                    onClick = onCategoryClick
                )
        )
    }
}

@Composable
fun DayCompletionCard(
    isDayEnded: Boolean,
    onToggleDayEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDayEnded) Color(0xFF7E6DF8) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .clickable { onToggleDayEnded() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (isDayEnded) "Day completed!" else "End the day",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isDayEnded) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CategoryListItem(
    category: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val cardColor = if (isSelected) {
        Color(0xFF7E6DF8)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun NumberCard(
    number: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}