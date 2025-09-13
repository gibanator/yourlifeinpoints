package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.TopBarController
import com.example.lifeinpoints.core.ui.TopBarState
import com.example.lifeinpoints.daily_checkup.data.Category

@Composable
fun CategoriesList(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategoryClick: (Category) -> Unit,
    topBar: TopBarController,
    viewModel: DailyCheckupViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        topBar.set(
            TopBarState(
                title = "Point Distribution",
                actions = {}
            )
        )
    }

    // Collect state from ViewModel
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val isDayEnded by viewModel.isDayEnded.collectAsState()
    val isMultiplierMode by viewModel.isMultiplierMode.collectAsState()

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Место для будущего календаря (уменьшенное)
            CalendarPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            // Карточка с категориями
            CategoryListCard(
                categories = categories,
                selectedCategories = selectedCategories,
                isDayEnded = isDayEnded,
                isMultiplierMode = isMultiplierMode,
                onCategoryClick = { index, category ->
                    viewModel.toggleCategory(index)
                    onCategoryClick(category)
                },
                onToggleMultiplierMode = { viewModel.toggleMultiplierMode() },
                modifier = Modifier.weight(1f)
            )
        }

        // Карточка блокировки выбора
        DayCompletionCard(
            isDayEnded = isDayEnded,
            onToggleDayEnded = { viewModel.toggleDayEnded() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun CategoryListCard(
    categories: List<Category>,
    selectedCategories: Set<Int>,
    isDayEnded: Boolean,
    isMultiplierMode: Boolean,
    onCategoryClick: (Int, Category) -> Unit,
    onToggleMultiplierMode: () -> Unit,
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
            // Список категорий
            categories.forEachIndexed { index, category ->
                val isSelected = selectedCategories.contains(index)

                CategoryRow(
                    category = category,
                    isSelected = isSelected,
                    isDayEnded = isDayEnded,
                    onCategoryClick = { onCategoryClick(index, category) }
                )
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
                    onClick = {
                        // Пока ничего не делает
                    }
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
                    text = "Add comment",
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
                        Color(0xFF5A4FCF) // Более темный фиолетовый для режима умножения
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

// Остальные компоненты остаются без изменений
// CalendarPlaceholder, CategoryRow, NumberCard, DayCompletionCard, CategoryListItem

@Composable
fun CategoryRow(
    category: Category,
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
            containerColor = if (isDayEnded) Color(0xFF7E6DF8) else MaterialTheme.colorScheme.surface
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

// Заглушка для будущего календаря
@Composable
fun CalendarPlaceholder(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Calendar will be here",
                fontSize = 14.sp,  // Уменьшили размер шрифта
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryListItem(
    category: Category,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val cardColor = if (isSelected) {
        Color(0xFF7E6DF8)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
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
                text = stringResource(category.descriptionRes),
                fontSize = 18.sp,  // Уменьшили размер шрифта
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2  // Ограничили количество строк
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryListItemPreview() {
    val testCategory = Category(descriptionRes = R.string.category1)

    CategoryListItem(
        category = testCategory,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CategoriesListPreview() {
    val testCategories = listOf(
        Category(descriptionRes = R.string.category1),
        Category(descriptionRes = R.string.category2),
        Category(descriptionRes = R.string.category3),
        Category(descriptionRes = R.string.category4),
        Category(descriptionRes = R.string.category5),

    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CategoriesList(
            categories = testCategories,
            modifier = Modifier.fillMaxSize(),
            onCategoryClick = { category ->
                // Обработка клика по категории
            },
            topBar = TopBarController()
        )
    }
}