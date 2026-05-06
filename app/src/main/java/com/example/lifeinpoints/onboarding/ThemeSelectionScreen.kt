package com.example.lifeinpoints.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.theme.LifeInPointsTheme
import com.example.lifeinpoints.core.ui.theme.ThemeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionScreen(
    onThemeSelected: (ThemeType) -> Unit
) {
    var selectedTheme by remember { mutableStateOf<ThemeType?>(null) }
    val previewTheme = selectedTheme ?: ThemeType.SYSTEM

    LifeInPointsTheme(themeType = previewTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.weight(0.3f))

                Text(
                    text = stringResource(R.string.theme_selection_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(0.1f))

                ThemeOptionCard(
                    title = stringResource(R.string.theme_preview_system_title),
                    description = stringResource(R.string.theme_preview_system_subtitle),
                    isSelected = selectedTheme == ThemeType.SYSTEM,
                    onClick = { selectedTheme = ThemeType.SYSTEM }
                )

                ThemeOptionCard(
                    title = stringResource(R.string.theme_preview_light_title),
                    description = stringResource(R.string.theme_preview_light_subtitle),
                    isSelected = selectedTheme == ThemeType.LIGHT,
                    onClick = { selectedTheme = ThemeType.LIGHT }
                )

                ThemeOptionCard(
                    title = stringResource(R.string.theme_preview_dark_title),
                    description = stringResource(R.string.theme_preview_dark_subtitle),
                    isSelected = selectedTheme == ThemeType.DARK,
                    onClick = { selectedTheme = ThemeType.DARK }
                )

                ThemeOptionCard(
                    title = stringResource(R.string.theme_preview_dark_stone_title),
                    description = stringResource(R.string.theme_preview_dark_stone_subtitle),
                    isSelected = selectedTheme == ThemeType.DARK_STONE,
                    onClick = { selectedTheme = ThemeType.DARK_STONE }
                )

                ThemeOptionCard(
                    title = stringResource(R.string.theme_preview_light_stone_title),
                    description = stringResource(R.string.theme_preview_light_stone_subtitle),
                    isSelected = selectedTheme == ThemeType.LIGHT_STONE,
                    onClick = { selectedTheme = ThemeType.LIGHT_STONE }
                )

                Spacer(modifier = Modifier.weight(0.3f))

                Button(
                    onClick = {
                        selectedTheme?.let { theme ->
                            onThemeSelected(theme)
                        }
                    },
                    enabled = selectedTheme != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.continue_button))
                }

                Spacer(modifier = Modifier.weight(0.2f))
            }
        }
    }
}

@Composable
fun ThemeOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}