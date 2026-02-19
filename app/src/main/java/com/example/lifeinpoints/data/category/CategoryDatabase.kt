// com.example.lifeinpoints.data.category/CategoryDatabase.kt
package com.example.lifeinpoints.data.category

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateDao
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateEntity
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressEntity
import com.example.lifeinpoints.data.daycompletion.DayCompletionDao
import com.example.lifeinpoints.data.daycompletion.DayCompletionEntity
import com.example.lifeinpoints.data.level.LevelProgressDao
import com.example.lifeinpoints.data.level.LevelProgressEntity
import com.example.lifeinpoints.data.level.SkillPointsDao
import com.example.lifeinpoints.data.level.SkillPointsEntity

@Database(
    entities = [
        CategoryEntity::class,
        DailyCategoryProgressEntity::class,
        DayCompletionEntity::class,
        CommentTemplateEntity::class,
        LevelProgressEntity::class, // Добавляем новую сущность
        SkillPointsEntity::class    // Добавляем сущность очков навыков
    ],
    version = 18, // Увеличиваем версию
    exportSchema = false
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun dailyProgressDao(): DailyCategoryProgressDao
    abstract fun dayCompletionDao(): DayCompletionDao
    abstract fun commentTemplateDao(): CommentTemplateDao
    abstract fun levelProgressDao(): LevelProgressDao // Добавляем DAO уровней
    abstract fun skillPointsDao(): SkillPointsDao // Добавляем DAO очков навыков
}