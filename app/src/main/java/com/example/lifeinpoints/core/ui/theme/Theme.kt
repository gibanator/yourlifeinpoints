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
        CompositionLocalProvider(
            LocalThemeType provides themeType,
            LocalStatsPalette provides StoneStatsPalette
        ) {
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

    CompositionLocalProvider(
        LocalThemeType provides themeType,
        LocalStatsPalette provides DefaultStatsPalette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}

private val DefaultStatsPalette = StatsPalette(
    month = Color(0xFF8BC34A),
    week  = Color(0xFFF44336),
    year  = Color(0xFF607D8B),
    categories = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFF44336),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4),
        Color(0xFF795548),
        Color(0xFF607D8B),
        Color(0xFFFFC107),
        Color(0xFFE91E63),
        Color(0xFF8BC34A),
        Color(0xFFCDDC39),
        Color(0xFFFFEB3B),
        Color(0xFFFF5722),
        Color(0xFF9E9E9E)
    )
)

private val StoneStatsPalette = StatsPalette(
    month = Color(0xFF6D7A6E),
    week  = Color(0xFF8A6F5A),
    year  = Color(0xFF5E6A73),
    categories = listOf(
        Color(0xFFD4AF37), // Золото (основной акцент)
        Color(0xFFB8860B), // Тёмное золото / бронза
        Color(0xFFC0C0C0), // Серебро / светлый камень
        Color(0xFFA9A9A9), // Тёмно-серый (сланец)
        Color(0xFF8B7355), // Кожа / песчаник
        Color(0xFF6B4F3C), // Коричневый (земля)
        Color(0xFF9E7B5E), // Светло-коричневый (известняк)
        Color(0xFF5D6D7E), // Серо-голубой (гранит)
        Color(0xFF4A6A6B), // Зелёно-серый (яшма)
        Color(0xFF6A4E3B), // Тёмная охра
        Color(0xFFA67B5B), // Медь / терракота
        Color(0xFF7E5E4D), // Красновато-коричневый (руда)
        Color(0xFFB2A68D), // Бежевый (песчаник)
        Color(0xFF8A7F6D), // Серо-бежевый (цемент)
        Color(0xFF5E4B3C)  // Тёмный шоколад (базальт)
    )
)