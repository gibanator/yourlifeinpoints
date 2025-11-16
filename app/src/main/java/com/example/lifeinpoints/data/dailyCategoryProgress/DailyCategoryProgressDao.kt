package com.example.lifeinpoints.data.dailyCategoryProgress

import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
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

    @Upsert
    suspend fun upsertAll(progressList: List<DailyCategoryProgressEntity>)

    @Query("SELECT * FROM daily_category_progress")
    fun observeAll(): Flow<List<DailyCategoryProgressEntity>>

    @Query("SELECT * FROM daily_category_progress WHERE categoryId = :categoryId")
    fun observeByCategoryId(categoryId: Int): Flow<List<DailyCategoryProgressEntity>>

    @Query("SELECT * FROM daily_category_progress WHERE date = :date")
    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity>

    @Query("DELETE FROM daily_category_progress WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("SELECT * FROM daily_category_progress WHERE categoryId = :categoryId AND date = :date")
    suspend fun getByCategoryAndDate(categoryId: Int, date: String): DailyCategoryProgressEntity?

    @Transaction
    suspend fun writeDay(
        date: String,
        completedIds: List<Int>,
        incompletedIds: List<Int>
    ) {
        val rows = buildList {
            completedIds.forEach { add(DailyCategoryProgressEntity(date = date, categoryId = it, value = true)) }
            incompletedIds.forEach { add(DailyCategoryProgressEntity(date = date, categoryId = it, value = false)) }
        }

        upsertAll(rows)
    }

    @Transaction
    suspend fun rewriteDay(
        date: String,
        completedIds: List<Int>,
        incompletedIds: List<Int>
    ) {
        deleteByDate(date)

        val rows = buildList {
            completedIds.forEach { add(DailyCategoryProgressEntity(date = date, categoryId = it, value = true)) }
            incompletedIds.forEach { add(DailyCategoryProgressEntity(date = date, categoryId = it, value = false)) }
        }

        insertAll(rows)
    }

    @Query("DELETE FROM daily_category_progress")
    suspend fun clearAll()
}