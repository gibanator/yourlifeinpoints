// com/example/lifeinpoints/core/ui/theme/StoneColors.kt
package com.example.lifeinpoints.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Базовые цвета камня
val StoneDarkGrey = Color(0xFF2C2C2C)
val StoneLightGrey = Color(0xFF5A5A5A)
val StoneMediumGrey = Color(0xFF3E3E3E)
val StoneAccent = Color(0xFF8B8B8B)
val StoneText = Color(0xFFDDDDDD)
val StoneTextDark = Color(0xFFAAAAAA)
val StoneSurface = Color(0xFF353535)
val StoneOutline = Color(0xFF6B6B6B)

// 🆕 Новые цвета для каменной темы
val StoneCardBackground = Color(0xFF4A4A4A)  // светлее стандартного surface
val StoneGold = Color(0xFFDAA520)            // золотой для выделения

@Composable
fun stoneColorScheme() = darkColorScheme(
    primary = StoneGold,                    // 🟡 золотой – основной акцент
    onPrimary = StoneDarkGrey,
    primaryContainer = StoneGold.copy(alpha = 0.2f),
    onPrimaryContainer = StoneGold,
    secondary = StoneLightGrey,
    onSecondary = StoneDarkGrey,
    secondaryContainer = StoneMediumGrey,
    onSecondaryContainer = StoneText,
    tertiary = StoneGold,                  // также золотой для акцентов
    onTertiary = StoneDarkGrey,
    background = StoneDarkGrey,
    onBackground = StoneText,
    surface = StoneCardBackground,         // 📦 теперь все surface-карточки светлее
    onSurface = StoneText,
    surfaceVariant = StoneCardBackground,  // тоже светлее
    onSurfaceVariant = StoneTextDark,
    outline = StoneOutline,
    error = Color(0xFFCF6679),
    onError = Color.Black
)