// EditCategoryScreen.kt
package com.example.lifeinpoints.categories

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.lifeinpoints.R

// com.example.lifeinpoints.categories/EditCategoryScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(
    categoryId: Int,
    onBack: () -> Unit,
    onCategoryUpdated: () -> Unit,
    onCategoryDeleted: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    var categoryName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val category = uiState.categories.firstOrNull { it.id == categoryId }


    LaunchedEffect(category?.name) {
        // only prefill if user hasn't typed yet
        if (categoryName.isBlank() && category != null) {
            categoryName = category.name
        }
    }

    val isStaticCategory = viewModel.isStaticCategory(categoryId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.category_edit_page_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Кнопка удаления (только для нестатических категорий)
                    if (!isStaticCategory) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !isLoading
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }

                    // Кнопка сохранения (только для нестатических категорий)
                    if (!isStaticCategory) {
                        IconButton(
                            onClick = {
                                if (categoryName.isNotBlank()) {
                                    isLoading = true
                                    errorMessage = null

                                    coroutineScope.launch {
                                        val result = viewModel.updateCategory(categoryId, categoryName.trim())
                                        isLoading = false

                                        if (result.isSuccess) {
                                            onCategoryUpdated()
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message
                                        }
                                    }
                                }
                            },
                            enabled = categoryName.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isStaticCategory) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "System Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "This is a system category. You can hide it from the main screen in the visibility settings, but you cannot edit its name or delete it.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Поле ввода для пользовательских категорий
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = {
                            categoryName = it
                            errorMessage = null
                        },
                        label = { Text(stringResource(R.string.category_name_field_title)) },
                        placeholder = { Text(stringResource(R.string.category_name_placeholder_text)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        isError = errorMessage != null
                    )

                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Диалог подтверждения удаления (только для нестатических категорий)
            if (showDeleteDialog && !isStaticCategory) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Category") },
                    text = { Text("Are you sure you want to delete \"$categoryName\"? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                coroutineScope.launch {
                                    val result = viewModel.deleteCategory(categoryId)
                                    if (result.isSuccess) {
                                        onCategoryDeleted()
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message
                                    }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}