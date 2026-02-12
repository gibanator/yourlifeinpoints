package com.example.lifeinpoints.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.lifeinpoints.core.ui.theme.LocalThemeType
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.core.ui.theme.isStoneTheme

/**
 * Делает цвет пастельнее (смягчает).
 * mix: 0..1 (чем больше, тем ближе к белому)
 * alphaMul: множитель альфы (чем меньше, тем “воздушнее”)
 */
fun Color.toPastel(
    mix: Float = 0.35f,
    alphaMul: Float = 0.9f
): Color {
    val r = red + (1f - red) * mix
    val g = green + (1f - green) * mix
    val b = blue + (1f - blue) * mix
    return Color(r, g, b, alpha = this.alpha * alphaMul)
}

/**
 * Смягчает цвет в "тяжёлых" темах (Stone, Dark, System+Dark).
 */
@Composable
fun Color.pastelIfNeeded(
    mix: Float = 0.35f,
    alphaMul: Float = 0.9f
): Color {
    val themeType = LocalThemeType.current
    val systemDark = isSystemInDarkTheme()

    val usePastel =
        themeType.isStoneTheme ||
                themeType == ThemeType.DARK ||
                (themeType == ThemeType.SYSTEM && systemDark)

    return if (usePastel) toPastel(mix, alphaMul) else this
}