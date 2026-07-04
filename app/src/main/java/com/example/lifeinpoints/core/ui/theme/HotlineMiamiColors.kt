// com/example/lifeinpoints/core/ui/theme/HotlineMiamiColors.kt
package com.example.lifeinpoints.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🌴 Палитра в стиле Hotline Miami — максимум неона на почти чёрном фиолетовом фоне.
// Фон намеренно очень тёмный, чтобы неоновые акценты «горели» ярче.
val HotlineBackground = Color(0xFF0B021A)       // почти чёрный фиолетовый
val HotlineSurface = Color(0xFF190833)          // поверхность карточек
val HotlineSurfaceVariant = Color(0xFF271152)   // приподнятые элементы

val HotlinePink = Color(0xFFFF2E97)             // 💗 неоновый маджента — основной акцент
val HotlineCyan = Color(0xFF00F0FF)             // 🩵 яркий неоновый циан
val HotlinePurple = Color(0xFFC15CFF)           // 💜 неоновый фиолетовый
val HotlineYellow = Color(0xFFFFD319)           // 🌇 закатный жёлтый

val HotlineText = Color(0xFFFBEBFF)             // почти белый с розовым отливом
val HotlineTextDim = Color(0xFFC6A6F0)          // приглушённый лавандовый
val HotlineOutline = Color(0xFFFF4FB0)          // 💗 неоновый розовый контур
val HotlineOutlineVariant = Color(0xFF5A2E8F)   // неоновый фиолетовый делитель

val HotlineNearBlack = Color(0xFF0B0016)        // тёмный «текст на неоне»

@Composable
fun hotlineMiamiColorScheme() = darkColorScheme(
    primary = HotlinePink,
    onPrimary = HotlineNearBlack,
    primaryContainer = HotlinePink.copy(alpha = 0.28f),
    onPrimaryContainer = HotlinePink,

    secondary = HotlineCyan,
    onSecondary = HotlineNearBlack,
    secondaryContainer = HotlineCyan.copy(alpha = 0.24f),
    onSecondaryContainer = HotlineCyan,

    tertiary = HotlinePurple,
    onTertiary = HotlineNearBlack,
    tertiaryContainer = HotlinePurple.copy(alpha = 0.26f),
    onTertiaryContainer = HotlinePurple,

    background = HotlineBackground,
    onBackground = HotlineText,
    surface = HotlineSurface,
    onSurface = HotlineText,
    surfaceVariant = HotlineSurfaceVariant,
    onSurfaceVariant = HotlineTextDim,

    outline = HotlineOutline,
    outlineVariant = HotlineOutlineVariant,

    inversePrimary = HotlineCyan,
    scrim = Color(0xFF000000),

    error = Color(0xFFFF3B6B),
    onError = HotlineNearBlack
)

// ☀️ Светлый вариант — насыщенный вейпорвейв-неон на розово-лавандовом фоне
val HotlineLightBackground = Color(0xFFFCE1F0)       // насыщенная бледная роза
val HotlineLightSurface = Color(0xFFFFF0F8)          // поверхность карточек
val HotlineLightSurfaceVariant = Color(0xFFF9CFE6)   // приподнятые элементы

val HotlineLightPink = Color(0xFFFF0088)             // 💗 неоновая маджента — основной акцент
val HotlineLightCyan = Color(0xFF008FA6)             // 🩵 насыщенный тил
val HotlineLightPurple = Color(0xFF8A2BE2)           // 💜 яркий фиолетовый
val HotlineLightOrange = Color(0xFFFF6A00)           // 🌇 неоновый оранжевый

val HotlineLightText = Color(0xFF2A0A3A)             // почти чёрный фиолетовый
val HotlineLightTextDim = Color(0xFF7A4A8C)          // приглушённый лавандовый
val HotlineLightOutline = Color(0xFFFF199B)          // 💗 неоновый розовый контур
val HotlineLightOutlineVariant = Color(0xFFF3B6DC)   // мягкий розовый делитель

@Composable
fun hotlineMiamiLightColorScheme() = lightColorScheme(
    primary = HotlineLightPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFC7E4),
    onPrimaryContainer = Color(0xFF7A0040),

    secondary = HotlineLightCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8ECF3),
    onSecondaryContainer = Color(0xFF033E49),

    tertiary = HotlineLightPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEAD1FF),
    onTertiaryContainer = Color(0xFF2E0866),

    background = HotlineLightBackground,
    onBackground = HotlineLightText,
    surface = HotlineLightSurface,
    onSurface = HotlineLightText,
    surfaceVariant = HotlineLightSurfaceVariant,
    onSurfaceVariant = HotlineLightTextDim,

    outline = HotlineLightOutline,
    outlineVariant = HotlineLightOutlineVariant,

    inversePrimary = HotlineLightPink,
    scrim = Color(0xFF000000),

    error = Color(0xFFC4003D),
    onError = Color.White
)
