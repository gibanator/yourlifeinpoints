package com.example.lifeinpoints.data.dailyCategoryProgress


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeinpoints.data.category.CategoryEntity

@Entity(
    tableName = "daily_category_progress",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["date"], unique = true),
        Index(value = ["categoryId"])
    ]
)
data class DailyCategoryProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val categoryId: Int,

    val date: String, // формат "YYYY-MM-DD"

    val value: Boolean
)