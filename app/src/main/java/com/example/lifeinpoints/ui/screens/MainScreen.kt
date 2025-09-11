package com.example.lifeinpoints.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifeinpoints.R // 💡 Важно: это должен быть ваш собственный R-класс
import com.example.lifeinpoints.ui.components.Category

@Composable
fun CategoriesList(
    categories: List<Category>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                // Обертка для элемента списка с квадратной карточкой слева
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Квадратная карточка с цифрой 0
                    Card(
                        modifier = Modifier
                            .size(56.dp) // Квадратная форма
                            .padding(end = 8.dp), // Отступ справа
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "0",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Основная карточка с контентом
                    CategoryListItem(
                        category = category,
                        modifier = Modifier.weight(1f) // Занимает оставшееся пространство
                    )
                }
            }
        }
    }
}


@Composable
fun CategoryListItem(
    category: Category,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = modifier,
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .sizeIn(minHeight = 26.dp),
            verticalAlignment = Alignment.CenterVertically, // Выравнивание по вертикали
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
        Category(descriptionRes = R.string.category1), // Замените на ваши реальные ресурсы
        Category(descriptionRes = R.string.category2),
        Category(descriptionRes = R.string.category3),
        Category(descriptionRes = R.string.category4),
        Category(descriptionRes = R.string.category5)
    )

    // Замените на тему вашего приложения
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CategoriesList(
                categories = testCategories,
                modifier = Modifier.fillMaxSize()
            )
        }

}
