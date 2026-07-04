package com.example.lifeinpoints.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val StoneTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.5f),
            offset = Offset(2f, 2f),
            blurRadius = 4f
        )
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        /*shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )*/
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(0.8f, 0.8f),
            blurRadius = 1.5f
        )
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(1f, 1f),
            blurRadius = 2f
        )
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(0.8f, 0.8f),
            blurRadius = 1.5f
        )
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.3f),
            offset = Offset(0.8f, 0.8f),
            blurRadius = 1.5f
        )
    )
)

// 🌴 Типографика Hotline Miami — жирные заголовки с неоновым свечением.
// Билдер переиспользуется тёмным и светлым вариантами (меняются цвета и сила свечения).
private fun neonGlow(color: Color, radius: Float, alpha: Float) = Shadow(
    color = color.copy(alpha = alpha),
    offset = Offset(0f, 0f),
    blurRadius = radius
)

private fun hotlineTypography(
    primaryGlow: Color,
    secondaryGlow: Color,
    glowAlpha: Float,
    blurScale: Float
): Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 1.sp,
        shadow = neonGlow(primaryGlow, 24f * blurScale, glowAlpha)
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 1.sp,
        shadow = neonGlow(primaryGlow, 20f * blurScale, glowAlpha)
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(primaryGlow, 18f * blurScale, glowAlpha)
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(primaryGlow, 16f * blurScale, glowAlpha)
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(secondaryGlow, 14f * blurScale, glowAlpha)
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(secondaryGlow, 12f * blurScale, glowAlpha)
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(primaryGlow, 10f * blurScale, glowAlpha)
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.4.sp,
        shadow = neonGlow(primaryGlow, 8f * blurScale, glowAlpha)
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.3.sp,
        shadow = neonGlow(secondaryGlow, 7f * blurScale, glowAlpha * 0.7f)
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        shadow = neonGlow(secondaryGlow, 6f * blurScale, glowAlpha * 0.55f)
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        shadow = neonGlow(secondaryGlow, 5f * blurScale, glowAlpha * 0.5f)
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        shadow = neonGlow(secondaryGlow, 4f * blurScale, glowAlpha * 0.45f)
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.8.sp,
        shadow = neonGlow(primaryGlow, 8f * blurScale, glowAlpha * 0.7f)
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp,
        shadow = neonGlow(primaryGlow, 7f * blurScale, glowAlpha * 0.65f)
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp,
        shadow = neonGlow(primaryGlow, 6f * blurScale, glowAlpha * 0.6f)
    )
)

// Тёмный неон — насыщенное, яркое свечение
val HotlineTypography = hotlineTypography(
    primaryGlow = HotlinePink,
    secondaryGlow = HotlineCyan,
    glowAlpha = 0.95f,
    blurScale = 1.25f
)

// Светлый неон — свечение мягче и темнее, чтобы текст оставался читаемым
val HotlineLightTypography = hotlineTypography(
    primaryGlow = HotlineLightPink,
    secondaryGlow = HotlineLightPurple,
    glowAlpha = 0.45f,
    blurScale = 0.7f
)