package com.example.lifeinpoints.data.dailyCategoryProgress


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.lifeinpoints.data.category.CategoryEntity

@Entity(
    tableName = "daily_category_progress",
    primaryKeys = ["date", "categoryLocalId"],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["localId"],
            childColumns = ["categoryLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryLocalId"])
    ]
)
data class DailyCategoryProgressEntity(
    val categoryLocalId: Int,

    val date: String,

    val value: Boolean,

    val comment: String?
)
