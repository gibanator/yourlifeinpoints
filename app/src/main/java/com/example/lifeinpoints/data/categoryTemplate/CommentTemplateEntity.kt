package com.example.lifeinpoints.data.categoryTemplate

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeinpoints.data.category.CategoryEntity

@Entity(
    tableName = "comment_templates",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("categoryId"),
        Index(value = ["categoryId", "position"], unique = true)
    ]
)
data class CommentTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val position: Int, // 0..4
    val text: String
)