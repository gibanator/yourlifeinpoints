package com.example.lifeinpoints.daily_checkup.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    onBack: () -> Unit,
    vm: DailyCheckupViewModel
) {
    val uiState by vm.uiState.collectAsState()
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM yyyy") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(uiState.selectedDate.format(formatter)) },
                navigationIcon = {
                    IconButton(onClick = { vm.commitCommentsAndLeave(onBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection))
                .verticalScroll(scrollState)
            .fillMaxSize(),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.orderedCategories.forEachIndexed { index, category ->
                val id = category.id
                val isSelected = id in uiState.selectedCategories
                val value = uiState.commentDrafts[id].orEmpty()

                val templates = vm.getTemplatesForCategory(id)
                Log.d("TEMPLATES", "${templates.size} found for category $id")

                OneComment(
                    category = category.name,
                    isSelected = isSelected,
                    value = value,
                    templates = templates,
                    onValueChange = { vm.onCommentChanged(id, it) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < uiState.orderedCategories.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun OneComment(
    category: String,
    value: String,
    templates: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val cardColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    var menuOpen by remember { mutableStateOf(false) }
    val menuItems = remember(templates) { templates.filter { it.isNotBlank() } }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = category,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                color = textColor
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = value,
                    onValueChange = { onValueChange(it.take(100)) },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (menuItems.isNotEmpty()) {
                            IconButton(onClick = { menuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Choose template",
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        unfocusedIndicatorColor = if (isSelected) Color.White.copy(alpha = 0.5f) else Color.LightGray,
                        focusedTextColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface,
                        cursorColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.comment_placeholder_text),
                            color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                        )
                    }
                )

                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    menuItems.forEach { template ->
                        DropdownMenuItem(
                            text = { Text(template, maxLines = 2) },
                            onClick = {
                                menuOpen = false
                                onValueChange(template) // replace
                                // or append:
                                // onValueChange(if (value.isBlank()) template else value + "\n" + template)
                            }
                        )
                    }
                }
            }
        }
    }
}
