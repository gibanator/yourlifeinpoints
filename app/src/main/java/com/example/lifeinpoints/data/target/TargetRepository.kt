package com.example.lifeinpoints.data.target

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetRepository @Inject constructor(
    private val dao: TargetDao,
    private val selectionDao: TargetSelectionDao
) {

    fun observeAll(): Flow<List<TargetEntity>> = dao.observeAll()

    suspend fun getSelectedForDate(date: String): Set<Int> =
        selectionDao.getSelectedForDate(date).toSet()

    fun observeSelectedForDate(date: String): Flow<List<Int>> =
        selectionDao.observeSelectedForDate(date)

    suspend fun addTarget(name: String, days: Int, deadline: LocalDate?) {
        val deadlineMillis = deadline?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        dao.insert(TargetEntity(name = name, days = days, deadlineMillis = deadlineMillis))
    }

    suspend fun deleteTarget(id: Int) = dao.deleteById(id)

    suspend fun updateTarget(id: Int, name: String, days: Int, deadline: LocalDate?) {
        val deadlineMillis = deadline?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        dao.update(id, name, days, deadlineMillis)
    }

    suspend fun completeTarget(id: Int) = dao.markCompleted(id)

    suspend fun extendTarget(id: Int, additionalDays: Int) = dao.addDays(id, additionalDays)

    suspend fun getGoalReachedTargets(): List<TargetEntity> = dao.getGoalReachedTargets()

    fun observeCompleted(): Flow<List<TargetEntity>> = dao.observeCompleted()

    fun observeCompletedDaysForTarget(targetId: Int): Flow<List<String>> =
        selectionDao.observeCompletedDaysForTarget(targetId)

    // Returns true if now selected, false if deselected
    suspend fun toggleSelection(targetId: Int, date: String): Boolean {
        val inserted = selectionDao.insert(TargetSelectionEntity(targetId, date))
        return if (inserted >= 0L) {
            dao.incrementDaysSelected(targetId)
            true
        } else {
            selectionDao.delete(targetId, date)
            dao.decrementDaysSelected(targetId)
            false
        }
    }
}
