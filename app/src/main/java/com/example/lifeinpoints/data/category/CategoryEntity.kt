package com.example.lifeinpoints.data.category

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeinpoints.data.user.UserEntity

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "name"], unique = true) // Уникальность имени в рамках пользователя
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val userId: Int, // связь с пользователем

    val color: String = "#6200EE", // цвет категории

    val icon: String = "", // иконка категории

    val sortOrder: Int = 0, // порядок сортировки

    val isActive: Boolean = true, // активна ли категория

    val createdAt: Long = System.currentTimeMillis() // дата создания
)