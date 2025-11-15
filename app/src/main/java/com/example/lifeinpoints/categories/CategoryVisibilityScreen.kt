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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

// com.example.lifeinpoints.categories/CategoryVisibilityScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryVisibilityScreen(
    onBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Отладочная информация
    LaunchedEffect(categoriesState.categories) {
        println("DEBUG: CategoryVisibilityScreen - Categories count: ${categoriesState.categories.size}")
        categoriesState.categories.forEach { category ->
            println("DEBUG: Category '${category.name}' - isVisible: ${category.isVisible}, isStatic: ${category.isStatic}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Visible Categories") },
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
            // Информационная карточка
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
                        text = "Visible Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Toggle which categories appear on the main screen. You can hide both system and user categories.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Список категорий с переключателями
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
                                viewModel.setCategoryVisibility(category.id, isVisible)
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
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (category.isStatic) {
                    Text(
                        text = "System category",
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