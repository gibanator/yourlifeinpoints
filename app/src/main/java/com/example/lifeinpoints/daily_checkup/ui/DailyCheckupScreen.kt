package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckupScreen(
    modifier: Modifier = Modifier,
    vm: DailyCheckupViewModel,
    onNavigateToComments: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM yyyy") }



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
                .padding(12.dp),
        ) {
            WeekBarWithButtons(
                days = uiState.currentWeek,
                onDaySelected = vm::onDaySelected,
                toPrevWeek = vm::toPrevWeek,
                toNextWeek = vm::toNextWeek,
            )
            Spacer(Modifier.height(12.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(Modifier.height(12.dp))
            // Карточка блокировки выбора
            DayCompletionCard(
                isDayEnded = uiState.isDayEnded,
                onToggleDayEnded = {
                    vm.toggleDayEnded()
                    vm.saveProgress() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left button
        IconButton(
            onClick = toPrevWeek) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous week")
        }

        // Center week row
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                val selected = day.isSelected
                Box(
                    modifier = Modifier
                        .weight(1f) // fixed circle size (safe for portrait/landscape)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { onDaySelected(day.date) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day.dayOfWeek,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = day.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Right button
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
            modifier = Modifier
                .padding(8.dp)
           ,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Список категорий
                categories.forEach { category ->
                    val isSelected = category.id in selectedCategories
                    val id = category.id

                    CategoryRow(
                        category = category.name,
                        isSelected = isSelected,
                        isDayEnded = isDayEnded,
                        onCategoryClick = { onCategoryClick(id) }
                    )
                }
            }

            // Добавляем пространство между списком категорий и кнопками
            Spacer(modifier = Modifier.height(8.dp))

            // Ряд с двумя кнопками
            ActionButtonsRow(
                selectedCount = selectedCategories.size,
                totalCount = if (isMultiplierMode) categories.size * 31 else categories.size,
                isDayEnded = isDayEnded,
                isMultiplierMode = isMultiplierMode,
                onToggleMultiplierMode = onToggleMultiplierMode,
                onAddComment = onAddComment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
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
        // Левая кнопка "Add comment"
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    //enabled = !isDayEnded,
                    onClick = onAddComment
                ),
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
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = "Add/Edit comments",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Правая кнопка с суммой выбранных категорий (теперь кликабельная)
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    onClick = onToggleMultiplierMode
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedCount > 0) {
                    if (isMultiplierMode) {
                        Color(0xFF7E6DF8) // Более темный фиолетовый для режима умножения
                    } else {
                        Color(0xFF7E6DF8) // Обычный фиолетовый
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
                    .padding(vertical = 10.dp)
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
            .height(60.dp),  // Фиксированная высота для каждой строки
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Квадратная карточка с цифрой 0 или 1
        NumberCard(
            number = if (isSelected) "1" else "0",
            modifier = Modifier
                .size(50.dp)  // Уменьшили размер
                .padding(end = 8.dp)
        )

        // Основная карточка с контентом
        CategoryListItem(
            category = category,
            isSelected = isSelected,
            modifier = Modifier
                .weight(1f)
                .height(50.dp)  // Фиксированная высота
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
                .padding(vertical = 12.dp, horizontal = 16.dp)  // Уменьшили отступы
                .clickable { onToggleDayEnded() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (isDayEnded) "Day completed!" else "End the day",
                fontSize = 20.sp,  // Уменьшили размер шрифта
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
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp), // закомментил потому что сильно мозолит глаза
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),  // Уменьшили отступы
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = category,
                fontSize = 18.sp,  // Уменьшили размер шрифта
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2  // Ограничили количество строк
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
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = number,
                fontSize = 18.sp,  // Уменьшили размер шрифта
                fontWeight = FontWeight.Bold
            )
        }
    }
}