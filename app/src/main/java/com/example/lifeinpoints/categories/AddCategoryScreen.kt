// com.example.lifeinpoints.categories/AddCategoryScreen.kt
package com.example.lifeinpoints.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onBack: () -> Unit,
    onCategoryAdded: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    var categoryName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Category") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (categoryName.isNotBlank()) {
                                isLoading = true
                                errorMessage = null

                                coroutineScope.launch {
                                    val result = viewModel.addCategory(categoryName.trim())
                                    isLoading = false

                                    if (result.isSuccess) {
                                        onCategoryAdded()
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
                            Icon(Icons.Filled.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле ввода названия категории
            OutlinedTextField(
                value = categoryName,
                onValueChange = {
                    categoryName = it
                    errorMessage = null
                },
                label = { Text("Category Name") },
                placeholder = { Text("Enter category name...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                isError = errorMessage != null
            )

            // Сообщение об ошибке
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Подсказка
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tips for creating categories:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Choose clear, descriptive names\n" +
                                "• Make categories specific and actionable\n" +
                                "• Avoid duplicate categories\n" +
                                "• Keep names short and memorable",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}