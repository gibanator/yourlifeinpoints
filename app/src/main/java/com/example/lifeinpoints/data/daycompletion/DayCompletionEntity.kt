// com.example.lifeinpoints.data.daycompletion/DayCompletionEntity.kt
package com.example.lifeinpoints.data.daycompletion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_completion")
data class DayCompletionEntity(
    @PrimaryKey
    val date: String, // формат "YYYY-MM-DD"
    val xpEarned: Int = 0
)