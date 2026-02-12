package com.example.lifeinpoints.categories.comment_templates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCommentTemplatesScreen(
    categoryId: Int,
    onBack: () -> Unit,
    vm: CommentTemplatesViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(categoryId) {
        vm.loadCategoryName(categoryId)
    }

    val categoryName by vm.categoryName.collectAsState()

    val templates by vm.observeTemplates(categoryId).collectAsState(initial = emptyList())
    val map = remember(templates) { templates.associateBy { it.position } }

    var editingPosition by remember { mutableStateOf<Int?>(null) }
    var editorText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(categoryName ?: stringResource(R.string.comment_templates_page_title)) },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.comment_template_annotation_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            (0..4).forEach { pos ->
                val text = map[pos]?.text.orEmpty()

                TemplateSlotCard(
                    index = pos,
                    text = text,
                    onEdit = {
                        editingPosition = pos
                        editorText = text
                    },
                    onClear = {
                        vm.clearSlot(categoryId, pos)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Editor dialog
        editingPosition?.let { pos ->
            AlertDialog(
                onDismissRequest = { editingPosition = null },
                title = { Text("${stringResource(R.string.template)} ${pos + 1}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editorText,
                            onValueChange = { editorText = it },
                            label = { Text(stringResource(R.string.text_word)) },
                            placeholder = { Text(stringResource(R.string.comment_template_text_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            minLines = 2,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                imeAction = ImeAction.Done
                            )
                        )
                        Text(
                            text = stringResource(R.string.empty_tip),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vm.saveSlot(categoryId, pos, editorText)
                            editingPosition = null
                        }
                    ) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { editingPosition = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun TemplateSlotCard(
    index: Int,
    text: String,
    onEdit: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEdit)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${stringResource(R.string.template)} ${index + 1}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = text.ifBlank { stringResource(R.string.empty) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            IconButton(
                onClick = onClear,
                enabled = text.isNotBlank()
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
        }
    }
}
