// com.example.lifeinpoints.data.daycompletion/DayCompletionDao.kt
package com.example.lifeinpoints.data.daycompletion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dayCompletion: DayCompletionEntity)

    @Query("SELECT * FROM day_completion WHERE date = :date")
    suspend fun getByDate(date: String): DayCompletionEntity?

    @Query("SELECT * FROM day_completion WHERE date = :date")
    fun observeByDate(date: String): Flow<DayCompletionEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM day_completion WHERE date = :date)")
    suspend fun exists(date: String): Boolean

    @Query("DELETE FROM day_completion WHERE date = :date")
    suspend fun deleteByDate(date: String)

    @Query("SELECT * FROM day_completion ORDER BY date DESC")
    fun observeAllCompletedDays(): Flow<List<DayCompletionEntity>>

    @Query("SELECT * FROM day_completion WHERE date BETWEEN :fromDate AND :toDate")
    fun observeRange(fromDate: String, toDate: String): Flow<List<DayCompletionEntity>>
}