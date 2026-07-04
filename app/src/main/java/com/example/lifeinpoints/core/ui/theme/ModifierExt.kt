// com/example/lifeinpoints/core/ui/theme/ModifierExt.kt
package com.example.lifeinpoints.core.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.Composable

// com/example/lifeinpoints/core/ui/theme/ModifierExt.kt
@Composable
fun Modifier.clipByTheme(
    stoneShape: Shape = RectangleShape,
    defaultShape: Shape
): Modifier {
    val isSharp = LocalThemeType.current.isSharpTheme
    return this.then(
        if (isSharp) Modifier.clip(stoneShape)
        else Modifier.clip(defaultShape)
    )
}