// com.example.lifeinpoints.di/LevelModule.kt
package com.example.lifeinpoints.di

import com.example.lifeinpoints.data.level.LevelProgressDao
import com.example.lifeinpoints.data.level.LevelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LevelModule {
    @Provides
    @Singleton
    fun provideLevelRepository(
        levelProgressDao: LevelProgressDao
    ): LevelRepository = LevelRepository(levelProgressDao)
}