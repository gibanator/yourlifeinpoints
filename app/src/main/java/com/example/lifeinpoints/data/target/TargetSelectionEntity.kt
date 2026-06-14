package com.example.lifeinpoints.data.target

import androidx.room.Entity

@Entity(tableName = "target_selections", primaryKeys = ["targetId", "date"])
data class TargetSelectionEntity(
    val targetId: Int,
    val date: String
)
