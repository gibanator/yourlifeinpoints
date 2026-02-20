package com.example.lifeinpoints.data.level

import kotlin.math.pow

object LevelConstants {
    const val BASE_XP = 20 // b = 100

    // Таблица множителей для классов
    val CLASS_MULTIPLIERS = mapOf(
        "SWORDSMAN" to listOf(3, 3, 1, 2, 1, 2),
        "PALADIN" to listOf(3, 1, 2, 3, 1, 2),
        "CYBORG" to listOf(2, 1, 1, 2, 3, 3),
        "BERSERKER" to listOf(3, 2, 1, 2, 1, 3),
        "REAPER" to listOf(2, 2, 1, 3, 1, 3),
        "HERBALIST" to listOf(1, 2, 1, 2, 3, 3),
        "BARD" to listOf(1, 2, 3, 2, 3, 1),
        "MONK" to listOf(2, 3, 1, 3, 1, 2),
        "ENGINEER" to listOf(1, 3, 1, 2, 3, 2),
        "NETRUNNER" to listOf(1, 3, 2, 2, 3, 1),
        "MARKSMAN" to listOf(1, 3, 1, 2, 2, 3),
        "GENERAL" to listOf(1, 1, 3, 2, 3, 2)
    )

    fun getRequiredXpForLevel(level: Int): Int {
        return (BASE_XP * level.toDouble().pow(1.3)).toInt()
    }
}