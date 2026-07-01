package com.example.lifeinpoints.data.outbox

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(outboxEntity: OutboxOperationEntity): Long

    @Query("SELECT * FROM outbox_operations ORDER BY createdAt ASC LIMIT 1")
    suspend fun getOldest(): OutboxOperationEntity?

    @Query("SELECT * FROM outbox_operations ORDER BY createdAt ASC, id ASC LIMIT :limit")
    suspend fun getOldest(limit: Int): List<OutboxOperationEntity>

    @Query("DELETE FROM outbox_operations WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("""
    SELECT COUNT(*)
    FROM outbox_operations
    WHERE type = :type
      AND entityType = :entityType
      AND syncKey = :syncKey
    """)
    suspend fun countByTypeAndKey(
        type: OutboxOperationType,
        entityType: String,
        syncKey: String
    ): Int

    @Query("SELECT COUNT(*) FROM outbox_operations")
    fun observePendingCount(): Flow<Int>
}