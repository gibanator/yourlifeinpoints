package com.example.lifeinpoints.data.level

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: LevelProgressEntity)

    @Update
    suspend fun update(progress: LevelProgressEntity)

    @Query("SELECT * FROM level_progress WHERE id = 1")
    suspend fun get(): LevelProgressEntity?

    @Query("SELECT * FROM level_progress WHERE id = 1")
    fun observe(): Flow<LevelProgressEntity?>

    @Query("DELETE FROM level_progress")
    suspend fun clear()
}