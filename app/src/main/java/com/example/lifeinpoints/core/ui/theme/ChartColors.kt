package com.example.lifeinpoints.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * Палитра цветов для графиков.
 * В Stone делаем более пастельные (меньше насыщенности/контраста).
 */
@Composable
fun chartPalette(themeType: ThemeType): List<Color> {
    val scheme = MaterialTheme.colorScheme

    val pastel = themeType.isStoneTheme || themeType == ThemeType.DARK || (themeType == ThemeType.SYSTEM && isSystemInDarkTheme())

    val base = listOf(
        scheme.primary,
        scheme.secondary,
        scheme.tertiary,
        scheme.error,
        scheme.primaryContainer,
        scheme.secondaryContainer,
        scheme.tertiaryContainer,
    )


    return if (pastel) base.map { it.copy(alpha = 0.60f) } else base
}