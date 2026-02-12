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
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.4f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
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