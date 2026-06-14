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
import com.example.lifeinpoints.data.target.TargetDao
import com.example.lifeinpoints.data.target.TargetEntity
import com.example.lifeinpoints.data.target.TargetSelectionDao
import com.example.lifeinpoints.data.target.TargetSelectionEntity

@Database(
    entities = [
        CategoryEntity::class,
        DailyCategoryProgressEntity::class,
        DayCompletionEntity::class,
        CommentTemplateEntity::class,
        LevelProgressEntity::class,
        SkillPointsEntity::class,
        TargetEntity::class,
        TargetSelectionEntity::class
    ],
    version = 21,
    exportSchema = false
)
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun dailyProgressDao(): DailyCategoryProgressDao
    abstract fun dayCompletionDao(): DayCompletionDao
    abstract fun commentTemplateDao(): CommentTemplateDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun skillPointsDao(): SkillPointsDao
    abstract fun targetDao(): TargetDao
    abstract fun targetSelectionDao(): TargetSelectionDao
}