package com.example.lifeinpoints.data.dailyCategoryProgress

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCategoryProgressDao {

    @Query("""
        SELECT *
        FROM daily_category_progress
        WHERE date = :date
    """)
    fun observeByDate(date: String): Flow<List<DailyCategoryProgressEntity>>

    @Query("""
        SELECT *
        FROM daily_category_progress
        WHERE date = :date
    """)
    suspend fun getByDate(date: String): List<DailyCategoryProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rows: List<DailyCategoryProgressEntity>)

    @Query("""
        DELETE FROM daily_category_progress
        WHERE date = :date
    """)
    suspend fun deleteByDate(date: String)

    @Transaction
    suspend fun rewriteDay(
        date: String,
        rows: List<DailyCategoryProgressEntity>
    ) {
        deleteByDate(date)
        upsertAll(rows)
    }
}
