// com.example.lifeinpoints.di/DatabaseModule.kt (обновлённая версия)
package com.example.lifeinpoints.di

import android.content.Context
import androidx.room.Room
import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.category.CategoryDatabase
import com.example.lifeinpoints.data.categoryTemplate.CommentTemplateDao
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.daycompletion.DayCompletionDao
import com.example.lifeinpoints.data.level.LevelProgressDao
import com.example.lifeinpoints.data.level.SkillPointsDao
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
    fun provideDatabase(@ApplicationContext context: Context): CategoryDatabase =
        Room.databaseBuilder(context, CategoryDatabase::class.java, "category_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCategoryDao(db: CategoryDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideDailyProgressDao(db: CategoryDatabase): DailyCategoryProgressDao = db.dailyProgressDao()

    @Provides
    fun provideDayCompletionDao(db: CategoryDatabase): DayCompletionDao = db.dayCompletionDao()

    @Provides
    fun provideCommentTemplateDao(db: CategoryDatabase): CommentTemplateDao = db.commentTemplateDao()

    // Добавляем новые DAO для системы уровней
    @Provides
    fun provideLevelProgressDao(db: CategoryDatabase): LevelProgressDao = db.levelProgressDao()

    @Provides
    fun provideSkillPointsDao(db: CategoryDatabase): SkillPointsDao = db.skillPointsDao()
}