// EditCategoryScreen.kt
package com.example.lifeinpoints.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
import kotlinx.coroutines.launch

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
            AppTopAppBar(
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
                                    coroutineScope.launch {
                                        val result = viewModel.updateCategory(categoryId, categoryName.trim())

                                        if (result.isSuccess) {
                                            onCategoryUpdated()
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