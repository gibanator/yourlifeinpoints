// com/example/lifeinpoints/core/ui/theme/ModifierExt.kt
package com.example.lifeinpoints.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.Composable

@Composable
fun Modifier.clipByTheme(
    stoneShape: Shape = RectangleShape, // для каменной темы
    defaultShape: Shape                // для остальных
): Modifier {
    val isStone = LocalThemeType.current == ThemeType.STONE
    return this.then(
        if (isStone) Modifier.clip(stoneShape)
        else Modifier.clip(defaultShape)
    )
}