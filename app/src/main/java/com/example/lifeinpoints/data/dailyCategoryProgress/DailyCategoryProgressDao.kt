package com.example.lifeinpoints.data.dailyCategoryProgress

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCategoryProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: DailyCategoryProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progressList: List<DailyCategoryProgressEntity>)

    @Update
    suspend fun update(progress: DailyCategoryProgressEntity)

    @Delete
    suspend fun delete(progress: DailyCategoryProgressEntity)

    @Query("SELECT * FROM daily_category_progress")
    fun observeAll(): Flow<List<DailyCategoryProgressEntity>>

    @Query("SELECT * FROM daily_category_progress WHERE id = :id")
    suspend fun getById(id: Int): DailyCategoryProgressEntity?

    @Query("SELECT * FROM daily_category_progress WHERE categoryId = :categoryId")
    fun observeByCategoryId(categoryId: Int): Flow<List<DailyCategoryProgressEntity>>

    @Query("SELECT * FROM daily_category_progress WHERE date = :date")
    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity>

    @Query("SELECT * FROM daily_category_progress WHERE categoryId = :categoryId AND date = :date")
    suspend fun getByCategoryAndDate(categoryId: Int, date: String): DailyCategoryProgressEntity?

    @Query("DELETE FROM daily_category_progress")
    suspend fun clearAll()
}