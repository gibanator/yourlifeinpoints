package com.example.lifeinpoints.data.level

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val currentLevel: Int = 0,
    val currentXp: Int = 0,
    val totalXp: Int = 0,

    // Неиспользованные очки навыков
    val unspentSkillPoints: Int = 0,

    // Значения навыков
    val strength: Int = 0,
    val agility: Int = 0,
    val charisma: Int = 0,
    val will: Int = 0,
    val intelligence: Int = 0,
    val survival: Int = 0,

    // Для отслеживания последовательных дней
    val consecutiveDays: Int = 0,
    val lastCompletedDate: String? = null // Формат "YYYY-MM-DD"
)