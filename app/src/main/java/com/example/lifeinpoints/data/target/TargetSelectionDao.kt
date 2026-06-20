package com.example.lifeinpoints.data.target

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetSelectionDao {
    @Query("SELECT targetId FROM target_selections WHERE date = :date")
    suspend fun getSelectedForDate(date: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: TargetSelectionEntity): Long

    @Query("DELETE FROM target_selections WHERE targetId = :targetId AND date = :date")
    suspend fun delete(targetId: Int, date: String)

    @Query("SELECT date FROM target_selections WHERE targetId = :targetId ORDER BY date DESC")
    fun observeCompletedDaysForTarget(targetId: Int): Flow<List<String>>
}
