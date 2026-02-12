package com.example.lifeinpoints.core.ui.theme

enum class ThemeType {
    SYSTEM,    // Следовать системе
    LIGHT,     // Светлая тема
    DARK_STONE,   // тёмный камень
    LIGHT_STONE,   // светлый камень ,
    DARK
}

// Extension для удобства
val ThemeType.isStoneTheme: Boolean
    get() = this == ThemeType.DARK_STONE || this == ThemeType.LIGHT_STONE