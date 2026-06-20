package com.example.lifeinpoints.data.target

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    @Query("SELECT * FROM targets ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<TargetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(target: TargetEntity)

    @Query("DELETE FROM targets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE targets SET daysSelected = daysSelected + 1 WHERE id = :id")
    suspend fun incrementDaysSelected(id: Int)

    @Query("UPDATE targets SET daysSelected = MAX(0, daysSelected - 1) WHERE id = :id")
    suspend fun decrementDaysSelected(id: Int)

    @Query("UPDATE targets SET name = :name, days = :days, deadlineMillis = :deadlineMillis WHERE id = :id")
    suspend fun update(id: Int, name: String, days: Int, deadlineMillis: Long?)

    @Query("UPDATE targets SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Int)

    @Query("UPDATE targets SET days = days + :additional WHERE id = :id")
    suspend fun addDays(id: Int, additional: Int)

    @Query("SELECT * FROM targets WHERE daysSelected >= days AND isCompleted = 0")
    suspend fun getGoalReachedTargets(): List<TargetEntity>

    @Query("SELECT * FROM targets WHERE isCompleted = 1 ORDER BY createdAt ASC")
    fun observeCompleted(): Flow<List<TargetEntity>>
}
