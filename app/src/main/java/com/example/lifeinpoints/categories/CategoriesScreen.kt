package com.example.lifeinpoints.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onEditCategory: (Int) -> Unit = {}, // Добавляем параметр для редактирования
    onManageVisibility: () -> Unit = {},
    vm: CategoriesViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var categoryToDelete by remember { mutableStateOf<CategoryUiItem?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.loadCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Кнопка управления видимостью
                    IconButton(onClick = onManageVisibility) {
                        Icon(Icons.Default.Visibility, contentDescription = "Manage Visibility")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCategory
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->
        Box{
            when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.categories.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No categories yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap the + button to add your first category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            else -> {
                CategoriesList(
                    categories = uiState.categories,
                    paddingValues = paddingValues,
                    onEditCategory = { category ->
                        onEditCategory(category.id)
                    },
                    onDeleteCategory = { category ->
                        categoryToDelete = category
                    }
                )

            }
            }
            // Диалог подтверждения удаления
            // CategoriesScreen.kt - в Scaffold content
            categoryToDelete?.let { category ->
                DeleteConfirmationDialog(
                    category = category,
                    onConfirm = {
                        coroutineScope.launch {
                            val result = vm.deleteCategory(category.id)
                            if (result.isSuccess) {
                                // Категория удалена, StateFlow автоматически обновит UI
                                categoryToDelete = null
                            } else {
                                // Можно показать ошибку
                                println("Failed to delete category: ${result.exceptionOrNull()?.message}")
                                categoryToDelete = null
                            }
                        }
                    },
                    onDismiss = { categoryToDelete = null }
                )
            }
        }
    }
}

@Composable
fun CategoriesList(
    categories: List<CategoryUiItem>,
    paddingValues: PaddingValues,
    onEditCategory: (CategoryUiItem) -> Unit,
    onDeleteCategory: (CategoryUiItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryCard(
                category = category,
                onEdit = { onEditCategory(category) },
                onDelete = { onDeleteCategory(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryUiItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp), // Минимальная высота, но может быть больше
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Показываем меню только для нестатических категорий
            if (!category.isStatic) {
                Box {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            } else {
                // Для статических категорий добавляем невидимый элемент
                Spacer(modifier = Modifier.size(48.dp)) // Занимаем такое же пространство
            }
        }
    }
}

// CategoriesScreen.kt - добавим в composable функцию
@Composable
fun DeleteConfirmationDialog(
    category: CategoryUiItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Category") },
        text = { Text("Are you sure you want to delete \"${category.name}\"? This action cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}