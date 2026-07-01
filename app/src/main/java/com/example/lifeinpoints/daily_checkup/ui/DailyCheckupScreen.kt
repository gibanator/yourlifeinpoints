package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.lifeinpoints.R
import com.example.lifeinpoints.Settings.SettingsViewModel
import com.example.lifeinpoints.aiScreen.AiModeScreen
import com.example.lifeinpoints.core.ui.AppTopAppBar
import com.example.lifeinpoints.core.ui.category.categoryDisplayName
import com.example.lifeinpoints.core.ui.theme.clipByTheme
import com.example.lifeinpoints.level.LevelViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckupScreen(
    //modifier: Modifier = Modifier,
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
    var showAddTargetSheet by remember { mutableStateOf(false) }
    var editingTarget by remember { mutableStateOf<TargetUi?>(null) }
    var showCompletedTargets by remember { mutableStateOf(false) }

    // Состояние для скролла всего экрана
    val scrollState = rememberScrollState()

    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it } // when swipe ends
            .collect {
                when (pagerState.currentPage) {
                    0 -> vm.prevDay()
                    2 -> vm.nextDay()
                }
                pagerState.scrollToPage(1)
            }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(uiState.selectedDate.format(formatter)) },
                actions = {
                    OutlinedButton(onClick = { vm.goToToday() }) {
                        Text(stringResource(R.string.to_today_button_text))
                    }
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        BoxWithConstraints(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
                .fillMaxSize()
        ) {
            val hPad = maxWidth * 0.04f
            val vGap = maxHeight * 0.015f

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(vGap)
            ) {
                WeekBarWithButtons(
                    days = uiState.currentWeek,
                    onDaySelected = vm::onDaySelected,
                    toPrevWeek = vm::toPrevWeek,
                    toNextWeek = vm::toNextWeek,
                )

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = hPad),
                    pageSpacing = hPad
                ) { _ ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(vGap)
                    ) {
                        if (gameModeEnabled) {
                            XpProgressBar(
                                levelState = levelState,
                                onClick = { showSkillScreen = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        CategoryListCard(
                            categories = uiState.orderedCategories,
                            selectedCategories = uiState.selectedCategories,
                            isDayEnded = uiState.isDayEnded,
                            isMultiplierMode = uiState.isMultiplierMode,
                            onCategoryClick = { id -> vm.toggleCategory(id) },
                            onToggleMultiplierMode = { vm.toggleMultiplierMode() },
                            onAddComment = onNavigateToComments,
                            modifier = Modifier.fillMaxWidth()
                        )
                        TargetListCard(
                            targets = uiState.targets,
                            selectedTargets = uiState.selectedTargets,
                            isDayEnded = uiState.isDayEnded,
                            completedCount = uiState.completedTargets.size,
                            onTargetClick = { id -> vm.toggleTarget(id) },
                            onTargetSettingsClick = { target -> editingTarget = target },
                            onAddTarget = { showAddTargetSheet = true },
                            onViewCompleted = { showCompletedTargets = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AiModeCard(
                            onClick = { vm.showAiMode() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DayCompletionCard(
                            isDayEnded = uiState.isDayEnded,
                            onToggleDayEnded = {
                                vm.toggleDayEnded()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = vGap * 2)
                        )
                    }
                }
            }
        }

        val targetGoalEvents by vm.targetGoalReachedEvent.collectAsState()
        targetGoalEvents.firstOrNull()?.let { target ->
            TargetGoalReachedDialog(
                target = target,
                remainingCount = targetGoalEvents.size,
                completedTargetsCount = uiState.completedTargets.size,
                onComplete = { vm.completeTargetAndNext(target.id) },
                onExtend = { days -> vm.extendTargetAndNext(target.id, days) },
                onDismiss = { vm.consumeNextTargetGoalEvent() },
                onViewCompleted = { showCompletedTargets = true }
            )
        }

        if (showCompletedTargets) {
            AlertDialog(
                onDismissRequest = { showCompletedTargets = false },
                title = {
                    Text(
                        text = stringResource(R.string.completed_targets_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    if (uiState.completedTargets.isEmpty()) {
                        Text(stringResource(R.string.no_completed_targets), style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            uiState.completedTargets.forEach { t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• ${t.name}  (${t.daysSelected}/${t.days})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { vm.deleteTarget(t.id) }) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = stringResource(R.string.delete),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCompletedTargets = false }) { Text(stringResource(R.string.close_button)) }
                }
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

        if (showAddTargetSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddTargetSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AddTargetSheet(
                    onDismiss = { showAddTargetSheet = false },
                    onConfirm = { name, days, deadline ->
                        vm.addTarget(name, days, deadline)
                        showAddTargetSheet = false
                    }
                )
            }
        }

        editingTarget?.let { target ->
            ModalBottomSheet(
                onDismissRequest = { editingTarget = null },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                EditTargetSheet(
                    target = target,
                    onDismiss = { editingTarget = null },
                    onConfirm = { name, days, deadline ->
                        vm.updateTarget(target.id, name, days, deadline)
                        editingTarget = null
                    },
                    onDelete = {
                        vm.deleteTarget(target.id)
                        editingTarget = null
                    }
                )
            }
        }

        // AI режим — всплывающее окно снизу
        if (uiState.isAiModeVisible) {
            ModalBottomSheet(
                onDismissRequest = { vm.hideAiMode() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AiModeScreen(
                    onBack = { vm.hideAiMode() },
                    onSubmit = { vm.evaluateDayWithAi(it) },
                    isLoading = uiState.isAiLoading,
                    errorMessage = uiState.aiError
                )
            }
        }

        if (uiState.isVoiceRecognitionVisible) {
            ModalBottomSheet(
                onDismissRequest = { vm.hideVoiceRecognition() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                VoiceRecognitionScreen()
            }
        }

        // Альтернатива: плавающее диалоговое окно (не на весь экран, закрывается по тапу снаружи)
        // Раскомментируй этот блок и закомментируй ModalBottomSheet выше, чтобы переключиться.
        // Также нужно раскомментировать импорты:
        //   import androidx.compose.ui.window.Dialog
        //   import androidx.compose.ui.window.DialogProperties
        //
        // if (uiState.isAiModeVisible) {
        //     Dialog(
        //         onDismissRequest = { vm.hideAiMode() },
        //         properties = DialogProperties(usePlatformDefaultWidth = false)
        //     ) {
        //         Card(
        //             modifier = Modifier
        //                 .fillMaxWidth(0.9f)
        //                 .padding(vertical = 24.dp),
        //             shape = MaterialTheme.shapes.large,
        //             elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        //         ) {
        //             AiModeScreen(onBack = { vm.hideAiMode() })
        //         }
        //     }
        // }
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
                text = stringResource(R.string.level_up_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.level_up_reached, level),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.level_up_skill_points),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (unspentSkillPoints > 0) {
                    Text(
                        text = stringResource(R.string.level_up_unspent_points, unspentSkillPoints),
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
                    Text(stringResource(R.string.later_button))
                }

                Button(
                    onClick = {
                        onGoToSkills()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.distribute_button))
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
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.prev_week_description))
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
                        .clipByTheme(
                            stoneShape = RectangleShape, // квадрат (острые углы)
                            defaultShape = CircleShape  // круг для всех остальных тем
                        )
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
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.next_week_description))
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
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.card_title_productivity_points),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Список категорий - теперь без внутреннего скролла
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category.id in selectedCategories
                    CategoryRow(
                        category = categoryDisplayName(category.name, category.nameKey, category.isSystem),
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
                onToggleMultiplierMode = onToggleMultiplierMode,
                onAddComment = onAddComment,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TargetListCard(
    targets: List<TargetUi>,
    selectedTargets: Set<Int>,
    isDayEnded: Boolean,
    completedCount: Int,
    onTargetClick: (Int) -> Unit,
    onTargetSettingsClick: (TargetUi) -> Unit,
    onAddTarget: () -> Unit,
    onViewCompleted: () -> Unit,
    modifier: Modifier = Modifier
){
    BoxWithConstraints(modifier = modifier) {
        val gap = maxWidth * 0.03f
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.small
        ){
            Column(
                modifier = Modifier.padding(gap),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                Text(
                    text = stringResource(R.string.card_title_goals),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(gap)
                ) {
                    targets.forEach { target ->
                        TargetRow(
                            target = target,
                            isSelected = target.id in selectedTargets,
                            isDayEnded = isDayEnded,
                            onTargetClick = { onTargetClick(target.id) },
                            onSettingsClick = { onTargetSettingsClick(target) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap)
                ) {
                    TargetActionCard(
                        text = stringResource(R.string.add_target_button),
                        onClick = onAddTarget,
                        modifier = Modifier.weight(1f)
                    )
                    TargetActionCard(
                        text = if (completedCount > 0) stringResource(R.string.view_completed_with_count, completedCount) else stringResource(R.string.view_completed),
                        onClick = onViewCompleted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TargetActionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val vertPad = maxWidth * 0.044f
        val textSize = with(LocalDensity.current) { (maxWidth * 0.078f).toSp() }
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = vertPad)
            ) {
                Text(
                    text = text,
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TargetRow(
    target: TargetUi,
    isSelected: Boolean,
    isDayEnded: Boolean,
    onTargetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val rowHeight = maxWidth * 0.15f
        val iconSize = rowHeight * 0.84f
        val iconEndPad = maxWidth * 0.02f

        val urgencyColor: Color? = target.deadline?.let { deadline ->
            val daysUntilDeadline = (deadline.toEpochDay() - LocalDate.now().toEpochDay() + 1).toInt()
            val daysRemaining = target.days - target.daysSelected
            val diff = daysUntilDeadline - daysRemaining
            when {
                diff < 0 -> MaterialTheme.colorScheme.errorContainer
                diff < 3 -> Color(0xFFFFE0B2)
                else -> null
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = rowHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberTargetCard(
                current = target.daysSelected,
                total = target.days,
                modifier = Modifier
                    .size(iconSize)
                    .padding(end = iconEndPad)
                    .clickable(onClick = onSettingsClick)
            )
            CategoryListItem(
                category = target.name,
                isSelected = isSelected,
                height = iconSize,
                urgencyColor = urgencyColor,
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = !isDayEnded, onClick = onTargetClick)
            )
        }
    }
}

@Composable
fun ActionButtonsRow(
    selectedCount: Int,
    totalCount: Int,
    onToggleMultiplierMode: () -> Unit,
    onAddComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val gap = maxWidth * 0.033f
        val vertPad = maxWidth * 0.022f
        val textSize = with(LocalDensity.current) { (maxWidth * 0.039f).toSp() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onAddComment),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = vertPad)
                ) {
                    Text(
                        text = stringResource(R.string.comments_button_text),
                        fontSize = textSize,
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
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedCount > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = vertPad)
                ) {
                    Text(
                        text = "$selectedCount/$totalCount",
                        fontSize = textSize,
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
}

@Composable
fun CategoryRow(
    category: String,
    isSelected: Boolean,
    isDayEnded: Boolean,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val rowHeight = maxWidth * 0.15f
        val iconSize = rowHeight * 0.84f
        val iconEndPad = maxWidth * 0.02f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = rowHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberCard(
                number = if (isSelected) "1" else "0",
                modifier = Modifier
                    .size(iconSize)
                    .padding(end = iconEndPad)
            )
            CategoryListItem(
                category = category,
                isSelected = isSelected,
                height = iconSize,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        enabled = !isDayEnded,
                        onClick = onCategoryClick
                    )
            )
        }
    }
}

@Composable
fun AiModeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val pad = maxWidth * 0.044f
        val spacerW = maxWidth * 0.022f
        val textSize = with(LocalDensity.current) { (maxWidth * 0.056f).toSp() }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = pad, horizontal = pad)
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.ai_mode_screen_title),
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(spacerW))
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun VoiceRecognitionCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.voice_button_text),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = if (isDayEnded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
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
                text = if (isDayEnded)
                    stringResource(R.string.day_ended_button_text)
                else stringResource(R.string.end_day_button_text),
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
    height: Dp = 50.dp,
    urgencyColor: Color? = null,
) {
    val cardColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        urgencyColor != null -> urgencyColor
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.height(height),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = MaterialTheme.shapes.small
        
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
        shape = MaterialTheme.shapes.small
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            val fontSize = (maxWidth.value * 0.36f).sp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = number,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Composable
fun NumberTargetCard(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            val fontSize = (maxWidth.value * 0.28f).sp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "$current/$total",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
