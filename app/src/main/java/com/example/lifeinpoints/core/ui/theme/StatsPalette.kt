package com.example.lifeinpoints.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class StatsPalette(
    val month: Color,
    val week: Color,
    val year: Color,
    val categories: List<Color>
)

val LocalStatsPalette = staticCompositionLocalOf<StatsPalette> {
    error("StatsPalette not provided")
}