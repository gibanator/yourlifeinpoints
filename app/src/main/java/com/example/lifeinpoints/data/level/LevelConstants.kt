package com.example.lifeinpoints.data.level

object LevelConstants {
    const val BASE_XP = 100 // b = 100
    const val CONSECUTIVE_BONUS = 50 // k = 50
    const val SKILL_POINTS_PER_LEVEL = 5

    // Таблица множителей для классов
    val CLASS_MULTIPLIERS = mapOf(
        "Мечник" to listOf(3, 3, 1, 2, 1, 2),
        "Паладин" to listOf(3, 1, 2, 3, 1, 2),
        "Киборг" to listOf(2, 1, 1, 2, 3, 3),
        "Берсерк" to listOf(3, 2, 1, 2, 1, 3),
        "Жнец" to listOf(2, 2, 1, 3, 1, 3),
        "Травник" to listOf(1, 2, 1, 2, 3, 3),
        "Бард" to listOf(1, 2, 3, 2, 3, 1),
        "Монах" to listOf(2, 3, 1, 3, 1, 2),
        "Инженер" to listOf(1, 3, 1, 2, 3, 2),
        "Нетраннер" to listOf(1, 3, 2, 2, 3, 1),
        "Стрелок" to listOf(1, 3, 1, 2, 2, 3),
        "Генерал" to listOf(1, 1, 3, 2, 3, 2)
    )

    fun getRequiredXpForLevel(level: Int): Int {
        return (BASE_XP * Math.pow(level.toDouble(), 1.2)).toInt()
    }

    fun calculateDailyXp(selectedCount: Int, totalCount: Int): Int {
        return if (totalCount > 0) {
            (100.0 * selectedCount / totalCount).toInt()
        } else {
            0
        }
    }
}