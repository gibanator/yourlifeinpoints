package com.example.lifeinpoints.Settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.core.ui.theme.ThemeType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onCategoriesClick: () -> Unit = {}, // Новый параметр для навигации
    onVisibilityClick: () -> Unit = {},
    vm: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by vm.currentTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            ThemeSelectionCard(
                currentTheme = currentTheme,
                onThemeSelected = { theme -> vm.setTheme(theme) }
            )

            // Новая секция категорий
            Text(
                text = "Content",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            CategoriesCard(
                onCategoriesClick = onCategoriesClick,
                onVisibilityClick = onVisibilityClick
            )
        }
    }
}

// com.example.lifeinpoints.Settings/SettingsScreen.kt
@Composable
fun CategoriesCard(
    onCategoriesClick: () -> Unit,
    onVisibilityClick: () -> Unit // Новый параметр
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Основная строка категорий
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategoriesClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Manage your activity categories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Разделитель
            Divider()

            // Строка управления видимостью
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVisibilityClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Visible Categories",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Choose which categories appear on main screen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionCard(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThemeOption(
                title = "System Default",
                subtitle = "Follow system theme",
                isSelected = currentTheme == ThemeType.SYSTEM,
                onClick = { onThemeSelected(ThemeType.SYSTEM) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            ThemeOption(
                title = "Light",
                subtitle = "Always use light theme",
                isSelected = currentTheme == ThemeType.LIGHT,
                onClick = { onThemeSelected(ThemeType.LIGHT) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            ThemeOption(
                title = "Dark",
                subtitle = "Always use dark theme",
                isSelected = currentTheme == ThemeType.DARK,
                onClick = { onThemeSelected(ThemeType.DARK) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}