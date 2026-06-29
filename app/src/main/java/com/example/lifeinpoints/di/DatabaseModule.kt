// com.example.lifeinpoints.di/DatabaseModule.kt (обновлённая версия)
package com.example.lifeinpoints.di

import android.content.Context
import androidx.room.Room
import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.AppDatabase
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateDao
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.daycompletion.DayCompletionDao
import com.example.lifeinpoints.data.level.LevelProgressDao
import com.example.lifeinpoints.data.level.SkillPointsDao
import com.example.lifeinpoints.data.target.TargetDao
import com.example.lifeinpoints.data.target.TargetSelectionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "category_db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideDailyProgressDao(db: AppDatabase): DailyCategoryProgressDao = db.dailyProgressDao()

    @Provides
    fun provideDayCompletionDao(db: AppDatabase): DayCompletionDao = db.dayCompletionDao()

    @Provides
    fun provideCommentTemplateDao(db: AppDatabase): CommentTemplateDao = db.commentTemplateDao()

    // Добавляем новые DAO для системы уровней
    @Provides
    fun provideLevelProgressDao(db: AppDatabase): LevelProgressDao = db.levelProgressDao()

    @Provides
    fun provideSkillPointsDao(db: AppDatabase): SkillPointsDao = db.skillPointsDao()

    @Provides
    fun provideTargetDao(db: AppDatabase): TargetDao = db.targetDao()

    @Provides
    fun provideTargetSelectionDao(db: AppDatabase): TargetSelectionDao = db.targetSelectionDao()

    @Provides
    fun provideOutboxDao(db: AppDatabase) = db.outboxDao()
}