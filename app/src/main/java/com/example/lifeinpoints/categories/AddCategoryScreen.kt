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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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

    var startMode by remember { mutableStateOf(CategoryStartMode.TODAY) }
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }


    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                                    val createdAtForVisibility = when (startMode) {
                                        CategoryStartMode.TODAY -> LocalDate.now().toEpochMilliAtStartOfDay()
                                        CategoryStartMode.PICK_DATE -> pickedDate.toEpochMilliAtStartOfDay()
                                        CategoryStartMode.FROM_START -> 0L
                                    }


                                    val result = viewModel.addCategory(
                                        categoryName.trim(),
                                        createdAt = createdAtForVisibility
                                    )
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

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

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

            Text(
                text = "Category appears from",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )


            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {


                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = startMode == CategoryStartMode.TODAY,
                        onClick = { startMode = CategoryStartMode.TODAY }
                    )
                    Text("Today")
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = startMode == CategoryStartMode.PICK_DATE,
                        onClick = {
                            startMode = CategoryStartMode.PICK_DATE
                            showDatePicker = true
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            startMode = CategoryStartMode.PICK_DATE
                            showDatePicker = true
                        }
                    ) {
                        Text("Pick a date: $pickedDate")
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = startMode == CategoryStartMode.FROM_START,
                        onClick = { startMode = CategoryStartMode.FROM_START }
                    )
                    Text("From the start")
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    pickedDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}


private enum class CategoryStartMode {
    TODAY,
    PICK_DATE,
    FROM_START
}


private fun LocalDate.toEpochMilliAtStartOfDay(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return this.atStartOfDay(zoneId).toInstant().toEpochMilli()
}