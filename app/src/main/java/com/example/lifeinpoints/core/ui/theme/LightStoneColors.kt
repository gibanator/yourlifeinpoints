// com/example/lifeinpoints/core/ui/theme/LightStoneColors.kt
package com.example.lifeinpoints.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Цвета светлого камня
val LightStoneBackground = Color(0xFFF5F5F5)   // очень светлый серый
val LightStoneSurface = Color(0xFFE8E8E8)     // поверхность карточек
val LightStoneMediumGrey = Color(0xFFD6D6D6)  // средний серый
//val LightStoneDarkGrey = Color(0xFF9E9E9E)    // тёмно-серый
val LightStoneText = Color(0xFF2C2C2C)        // тёмный текст
val LightStoneTextLight = Color(0xFF666666)   // второстепенный текст
val LightStoneOutline = Color(0xFFBDBDBD)     // границы

// Золотой акцент (общий для обеих каменных тем)
val StoneGold = Color(0xFFDAA520)

@Composable
fun lightStoneColorScheme() = lightColorScheme(
    primary = StoneGold,
    onPrimary = Color.Black,
    primaryContainer = StoneGold.copy(alpha = 0.2f),
    onPrimaryContainer = StoneGold,
    secondary = LightStoneMediumGrey,
    onSecondary = LightStoneText,
    secondaryContainer = LightStoneSurface,
    onSecondaryContainer = LightStoneText,
    tertiary = StoneGold,
    onTertiary = Color.Black,
    background = LightStoneBackground,
    onBackground = LightStoneText,
    surface = LightStoneSurface,
    onSurface = LightStoneText,
    surfaceVariant = LightStoneSurface,
    onSurfaceVariant = LightStoneTextLight,
    outline = LightStoneOutline,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)