package com.example.lifeinpoints.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Theme.kt
// LifeInPointsTheme.kt
// com/example/lifeinpoints/core/ui/theme/Theme.kt
@Composable
fun LifeInPointsTheme(
    themeType: ThemeType = ThemeType.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 🪨 Каменные темы (острые углы, кастомная типографика)
    if (themeType.isStoneTheme) {
        CompositionLocalProvider(LocalThemeType provides themeType) {
            MaterialTheme(
                colorScheme = when (themeType) {
                    ThemeType.DARK_STONE -> darkStoneColorScheme()
                    ThemeType.LIGHT_STONE -> lightStoneColorScheme()
                    else -> error("Unreachable")
                },
                typography = StoneTypography,
                shapes = StoneShapes,
                content = content
            )
        }
        return
    }

    // Стандартные темы (SYSTEM/LIGHT/DARK) — без изменений
    val systemDarkTheme = isSystemInDarkTheme()
    val useDarkTheme = when (themeType) {
        ThemeType.SYSTEM -> systemDarkTheme
        ThemeType.LIGHT  -> false
        ThemeType.DARK   -> true
        else -> false // каменные уже отфильтрованы
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else         -> LightColorScheme
    }

    CompositionLocalProvider(LocalThemeType provides themeType) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}