package com.example.lifeinpoints.di

import android.content.Context
import androidx.room.Room
import com.example.lifeinpoints.data.CategoryDao
import com.example.lifeinpoints.data.CategoryDatabase
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
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideCategory(db: CategoryDatabase): CategoryDao = db.categoryDao()
}