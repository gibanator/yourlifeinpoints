// com.example.lifeinpoints.data.daycompletion/DayCompletionDao.kt
package com.example.lifeinpoints.data.daycompletion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DayCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dayCompletion: DayCompletionEntity)

    @Update
    suspend fun update(dayCompletion: DayCompletionEntity)

    @Query("SELECT * FROM day_completion WHERE date = :date")
    suspend fun getByDate(date: String): DayCompletionEntity?

    @Query("SELECT * FROM day_completion WHERE date = :date")
    fun observeByDate(date: String): Flow<DayCompletionEntity?>

    @Query("SELECT * FROM day_completion WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getByDateRange(startDate: String, endDate: String): List<DayCompletionEntity>

    @Query("SELECT * FROM day_completion WHERE isCompleted = 1 ORDER BY date DESC")
    fun observeAllCompletedDays(): Flow<List<DayCompletionEntity>>

    @Query("DELETE FROM day_completion WHERE date = :date")
    suspend fun deleteByDate(date: String)
}