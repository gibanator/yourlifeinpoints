package com.example.lifeinpoints.core.ui.theme

enum class ThemeType {
    SYSTEM,    // Следовать системе
    LIGHT,     // Светлая тема
    DARK_STONE,   // тёмный камень
    LIGHT_STONE,   // светлый камень ,
    DARK,
    HOTLINE_MIAMI,        // неоновый ретро (тёмный)
    HOTLINE_MIAMI_LIGHT   // неоновый ретро (светлый)
}

// Extension для удобства
val ThemeType.isStoneTheme: Boolean
    get() = this == ThemeType.DARK_STONE || this == ThemeType.LIGHT_STONE

val ThemeType.isHotlineTheme: Boolean
    get() = this == ThemeType.HOTLINE_MIAMI ||
            this == ThemeType.HOTLINE_MIAMI_LIGHT

// Светлый вариант Hotline Miami
val ThemeType.isLightHotline: Boolean
    get() = this == ThemeType.HOTLINE_MIAMI_LIGHT

// Темы с острыми углами, кастомной типографикой и «плоским» chrome
// (непрозрачный FAB, ровный TopAppBar). Каменные и Hotline Miami.
val ThemeType.isSharpTheme: Boolean
    get() = isStoneTheme || isHotlineTheme
