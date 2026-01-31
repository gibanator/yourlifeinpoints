package com.example.lifeinpoints.data.level

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillPointsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(skillPoints: SkillPointsEntity)

    @Update
    suspend fun update(skillPoints: SkillPointsEntity)

    @Query("SELECT * FROM skill_points WHERE skillType = :skillType")
    suspend fun getByType(skillType: String): SkillPointsEntity?

    @Query("SELECT * FROM skill_points")
    suspend fun getAll(): List<SkillPointsEntity>

    @Query("DELETE FROM skill_points")
    suspend fun clear()

    @Query("SELECT * FROM skill_points WHERE skillType = :skillType")
    fun observeByType(skillType: String): Flow<SkillPointsEntity?>

    @Query("SELECT * FROM skill_points")
    fun observeAll(): Flow<List<SkillPointsEntity>>
}