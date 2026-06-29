package com.example.lifeinpoints.data.category

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories"
//    foreignKeys = [
//        ForeignKey(
//            entity = UserEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["userId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [
//        Index(value = ["userId"]),
//        Index(value = ["userId", "name"], unique = true) // Уникальность имени в рамках пользователя
//    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    val serverId: Long? = null, // идентификатор на сервере
    val name: String,

    //val nameKey: String? = null,

    //val userId: Int, // связь с пользователем

    //val color: String = "#6200EE", // цвет категории

    //val icon: String = "", // иконка категории

    val sortOrder: Int = 0, // порядок сортировки

    val isActive: Boolean = true, // активна ли категория

    val createdAt: Long = System.currentTimeMillis(), // дата создания

    val updatedAt: Long = System.currentTimeMillis(), // дата обновления

    // Новое поле: является ли категория системной
    val isSystem: Boolean = false,

    // Новое поле: отображается ли категория на главном экране
    val isVisible: Boolean = true,

    val isDeleted: Boolean = false // маркер удаления локально, чтобы сервер тоже понял, что надо удалить
)