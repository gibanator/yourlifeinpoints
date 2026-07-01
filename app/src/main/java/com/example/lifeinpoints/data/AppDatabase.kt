package com.example.lifeinpoints.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.category.CategoryEntity
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
import com.example.lifeinpoints.data.outbox.OutboxDao
import com.example.lifeinpoints.data.target.TargetDao
import com.example.lifeinpoints.data.target.TargetEntity
import com.example.lifeinpoints.data.target.TargetSelectionDao
import com.example.lifeinpoints.data.target.TargetSelectionEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity

@Database(
    entities = [
        CategoryEntity::class,
        DailyCategoryProgressEntity::class,
        DayCompletionEntity::class,
        CommentTemplateEntity::class,
        LevelProgressEntity::class,
        SkillPointsEntity::class,
        TargetEntity::class,
        TargetSelectionEntity::class,
        OutboxOperationEntity::class
    ],
    version = 23,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun dailyProgressDao(): DailyCategoryProgressDao
    abstract fun dayCompletionDao(): DayCompletionDao
    abstract fun commentTemplateDao(): CommentTemplateDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun skillPointsDao(): SkillPointsDao
    abstract fun targetDao(): TargetDao
    abstract fun targetSelectionDao(): TargetSelectionDao

    abstract fun outboxDao(): OutboxDao
}