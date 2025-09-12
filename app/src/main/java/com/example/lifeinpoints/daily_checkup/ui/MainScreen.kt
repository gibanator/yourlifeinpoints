package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeinpoints.R
import com.example.lifeinpoints.daily_checkup.data.Category

@Composable
fun CategoriesList(
    categories: List<Category>,
    modifier: Modifier = Modifier,
    onCategoryClick: (Category) -> Unit
) {
    // Создаем состояние для отслеживания выбранных категорий
    val selectedStates = remember { mutableStateOf(emptySet<Int>()) }
    // Состояние для отслеживания завершения дня
    val isDayEnded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Увеличиваем расстояние между элементами
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, category ->
                    val isSelected = selectedStates.value.contains(index)
                    // Обертка для элемента списка с квадратной карточкой слева
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Квадратная карточка с цифрой 0 или 1
                        Card(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = if (isSelected) "1" else "0",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Основная карточка с контентом
                        CategoryListItem(
                            category = category,
                            isSelected = isSelected,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    enabled = !isDayEnded.value, // Блокировка при завершении дня
                                    onClick = {
                                        // Переключаем состояние при нажатии
                                        val newSelection = selectedStates.value.toMutableSet()
                                        if (isSelected) {
                                            newSelection.remove(index)
                                        } else {
                                            newSelection.add(index)
                                        }
                                        selectedStates.value = newSelection

                                        // Вызываем внешний обработчик
                                        onCategoryClick(category)
                                    }
                                )
                        )
                    }
                }
            }
        }

        // Карточка блокировки выбора (вынесена из основной карточки)
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDayEnded.value) Color(0xFF7E6DF8) else MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .sizeIn(minHeight = 26.dp)
                    .clickable {
                        // Переключаем состояние завершения дня
                        isDayEnded.value = !isDayEnded.value
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "End the day",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CategoryListItem(
    category: Category,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // Определяем цвет карточки в зависимости от состояния
    val cardColor = if (isSelected) {
        Color(0xFF7E6DF8)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp) // Добавляем закругление для отдельных карточек
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .sizeIn(minHeight = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(category.descriptionRes),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
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
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CategoriesListPreview() {
    // Создаем тестовые данные для превью
    val testCategories = listOf(
        Category(descriptionRes = R.string.category1),
        Category(descriptionRes = R.string.category2),
        Category(descriptionRes = R.string.category3),
        Category(descriptionRes = R.string.category4),
        Category(descriptionRes = R.string.category5)
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
            }
        )
    }
}