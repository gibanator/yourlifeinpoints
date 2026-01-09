package com.example.lifeinpoints.data.category

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressDao
import com.example.lifeinpoints.data.dailyCategoryProgress.DailyCategoryProgressEntity
import com.example.lifeinpoints.data.daycompletion.DayCompletionDao
import com.example.lifeinpoints.data.daycompletion.DayCompletionEntity


@Database(
    entities = [
        //UserEntity::class,                    // новая таблица
        CategoryEntity::class,                // обновленная (добавлен user_id)
        DailyCategoryProgressEntity::class,    // существующая
        DayCompletionEntity::class  // Добавляем новую сущность
    ],
    version = 11,
    exportSchema = false
)
abstract class CategoryDatabase : RoomDatabase() {
    //abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun dailyProgressDao(): DailyCategoryProgressDao // новый DAO
    abstract fun dayCompletionDao(): DayCompletionDao  // Добавляем новый DAO
}