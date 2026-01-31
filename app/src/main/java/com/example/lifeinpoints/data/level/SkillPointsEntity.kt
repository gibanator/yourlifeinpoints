package com.example.lifeinpoints.data.level

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skill_points")
data class SkillPointsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val skillType: String, // "strength", "agility", etc.
    val points: Int = 0
)