// com.example.lifeinpoints.categories.CategoryVisibilityScreen.kt
package com.example.lifeinpoints.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.statistics.StatisticsViewModel
import kotlinx.coroutines.launch
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
import com.example.lifeinpoints.core.ui.category.categoryDisplayName

// com.example.lifeinpoints.categories/CategoryVisibilityScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryVisibilityScreen(
    onBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
    statisticsViewModel: StatisticsViewModel = hiltViewModel() // Добавляем ViewModel статистики
) {
    val categoriesState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(stringResource(R.string.categories_visibility_page_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.categories_visibility_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.categories_visibility_annotation_text),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoriesState.categories) { category ->
                    CategoryVisibilityItem(
                        category = category,
                        onVisibilityChanged = { isVisible ->
                            coroutineScope.launch {
                                // Изменяем видимость категории
                                viewModel.setCategoryVisibility(category.id, isVisible)

                                // Принудительно обновляем статистику
                                statisticsViewModel.forceRefresh()
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CategoryVisibilityItem(
    category: CategoryUiItem,
    onVisibilityChanged: (Boolean) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) } // Будем получать из базы, но для простоты используем состояние

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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoryDisplayName(
                        category.name,
                        category.nameKey,
                        category.isStatic
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (category.isStatic) {
                    Text(
                        text = stringResource(R.string.system_category_annotation_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Переключатель видимости
            Switch(
                checked = category.isVisible,
                onCheckedChange = { newVisibility ->
                    onVisibilityChanged(newVisibility)
                },
                enabled = true
            )
        }
    }
}