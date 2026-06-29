package com.example.lifeinpoints.data.outbox

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.IOException

@Entity(tableName = "outbox_operations")
data class OutboxOperationEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val type: OutboxOperationType,

    val entityType: String,

    val entityLocalId: Int? = null,

    val syncKey: String? = null,  // идентификатор для синхронизации тех энтити, у которых нет своего идентификатора

    val createdAt: Long = System.currentTimeMillis(),
) {
    fun requireLocalId(): Int =
        entityLocalId ?: throw IOException("Operation $id has no localId")
    fun requireSyncKey(): String =
        syncKey ?: throw IOException("Operation $id has no syncKey")
}
