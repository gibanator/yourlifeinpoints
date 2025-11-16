package com.example.lifeinpoints.di

import android.content.Context
import androidx.room.Room
import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.category.CategoryDatabase
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.user.UserDao
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
            .fallbackToDestructiveMigration() // Для тестирования - пересоздаем БД
            .build()

    @Provides
    fun provideCategoryDao(db: CategoryDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideDailyProgressDao(db: CategoryDatabase): DailyCategoryProgressDao = db.dailyProgressDao()
    //    @Provides
//    fun provideUserDao(db: CategoryDatabase): UserDao = db.userDao()

}