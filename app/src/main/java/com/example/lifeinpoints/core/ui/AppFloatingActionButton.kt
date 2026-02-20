// com/example/lifeinpoints/core/ui/AppFloatingActionButton.kt
package com.example.lifeinpoints.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.core.ui.theme.LocalThemeType
import com.example.lifeinpoints.core.ui.theme.isStoneTheme

@Composable
fun AppFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val isStone = LocalThemeType.current.isStoneTheme

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp), // стандартный размер
        shape = MaterialTheme.shapes.extraLarge, // в каменной теме = 0dp (острые углы)
        containerColor = if (isStone) {
            MaterialTheme.colorScheme.primary      // непрозрачный золотой
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        contentColor = if (isStone) {
            MaterialTheme.colorScheme.onPrimary    // тёмный текст/иконка
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
    ) {
        icon()
    }
}