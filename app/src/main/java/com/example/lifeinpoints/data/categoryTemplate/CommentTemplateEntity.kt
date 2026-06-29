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
            parentColumns = ["localId"],
            childColumns = ["categoryLocalId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index("categoryLocalId"),
        Index(value = ["categoryLocalId", "position"], unique = true)
    ]
)
data class CommentTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val categoryLocalId: Int,

    val position: Int,
    val text: String
)
