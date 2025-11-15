// com.example.lifeinpoints.categories/CategoriesModule.kt
package com.example.lifeinpoints.categories

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoriesModule {

    @Provides
    @Singleton
    fun provideCategoriesRepository(): CategoriesRepository {
        return CategoriesRepository()
    }
}