package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    onBack: () -> Unit,
    vm: DailyCheckupViewModel = hiltViewModel() // Получаем ViewModel
) {
    val uiState by vm.uiState.collectAsState()
    val formatter = remember { DateTimeFormatter.ofPattern("EEE d MMM yyyy") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.selectedDate.format(formatter)) },
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
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.orderedCategories.forEachIndexed { index, category ->
                val isSelected = uiState.selectedCategories.contains(index)

                OneComment(
                    category = category.name,
                    isSelected = isSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                // Добавляем отступ после каждого комментария, кроме последнего
                if (index < uiState.orderedCategories.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Добавляем дополнительное пространство внизу для лучшей прокрутки
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun OneComment(
    category: String,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val textState = remember { mutableStateOf("") }

    // Используем тот же фиолетовый цвет, что и в DailyCheckupScreen
    val cardColor = if (isSelected) {
        Color(0xFF7E6DF8) // Тот же фиолетовый, что и для выбранных категорий
    } else {
        MaterialTheme.colorScheme.surface
    }

    // Цвет текста для заголовка
    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
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

            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier.fillMaxWidth(),
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
                        "Add your commentary...",
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )
                }
            )
        }
    }
}