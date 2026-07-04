// com/example/lifeinpoints/core/ui/AppFloatingActionButton.kt
package com.example.lifeinpoints.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeinpoints.core.ui.theme.LocalThemeType
import com.example.lifeinpoints.core.ui.theme.isSharpTheme

@Composable
fun AppFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val isSharp = LocalThemeType.current.isSharpTheme

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp), // стандартный размер
        shape = MaterialTheme.shapes.extraLarge, // в каменной/неоновой теме = острые углы
        containerColor = if (isSharp) {
            MaterialTheme.colorScheme.primary      // непрозрачный акцент
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        contentColor = if (isSharp) {
            MaterialTheme.colorScheme.onPrimary    // тёмный текст/иконка
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }
    ) {
        icon()
    }
}