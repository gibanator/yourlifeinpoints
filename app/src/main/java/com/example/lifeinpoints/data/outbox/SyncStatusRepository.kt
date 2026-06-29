package com.example.lifeinpoints.data.outbox

import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStatusRepository @Inject constructor(
    private val outboxDao: OutboxDao,
    private val auth: AuthTokenProvider
) {
    fun observeSyncStatus(): Flow<SyncStatus> {
        return outboxDao.observePendingCount()
            .map { pendingCount ->
                when {
                    !auth.isLoggedIn() -> SyncStatus.NotLoggedIn
                    pendingCount > 0 -> SyncStatus.Pending(pendingCount)
                    else -> SyncStatus.Synced
                }
            }
    }
}

sealed interface SyncStatus {
    data object NotLoggedIn : SyncStatus
    data object Synced : SyncStatus
    data class Pending(val count: Int) : SyncStatus
    data class Error(val message: String?) : SyncStatus
}