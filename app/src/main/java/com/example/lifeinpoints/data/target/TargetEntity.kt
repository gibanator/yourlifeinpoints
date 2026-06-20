package com.example.lifeinpoints.data.target

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "targets")
data class TargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val days: Int,
    val daysSelected: Int = 0,
    val isCompleted: Boolean = false,
    val deadlineMillis: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
