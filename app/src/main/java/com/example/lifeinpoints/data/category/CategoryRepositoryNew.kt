package com.example.lifeinpoints.data.category

import androidx.room.withTransaction
import com.example.lifeinpoints.data.AppDatabase
import com.example.lifeinpoints.data.outbox.OutboxDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryNew @Inject constructor(
    private val database: AppDatabase,
    private val categoryDao: CategoryDao,
    private val outboxDao: OutboxDao,
    private val syncScheduler: SyncScheduler
) {
    fun observeAll() = categoryDao.observeAll()

    fun observeVisibleCategories() = categoryDao.observeVisibleCategories()
    fun observeVisibleCategoriesCreatedBefore(
        fromTime: Long
    ): Flow<List<CategoryEntity>> =
        categoryDao.observeVisibleCategoriesCreatedBefore(fromTime)


    suspend fun initializeSystemCategories() {
        // Пока можно оставить пустым, если системные категории создаются локально/seed-ом.
    }

    suspend fun addCategory(
        name: String,
        createdAt: Long = System.currentTimeMillis()
    ) {
        val trimmedName = name.trim()
        require(trimmedName.isNotBlank()) { "Category name cannot be blank" }

        database.withTransaction {
            val categoryId = categoryDao.insert(
                CategoryEntity(
                    name = trimmedName,
                    createdAt = createdAt,
                    updatedAt = System.currentTimeMillis(),
                    isActive = true,
                    isVisible = true,
                    isSystem = false
                )
            ).toInt()

            outboxDao.insert(
                OutboxOperationEntity(
                    type = OutboxOperationType.CREATE_CATEGORY,
                    entityType = CATEGORY_ENTITY_TYPE,
                    entityLocalId = categoryId
                )
            )
        }

        syncScheduler.enqueueSync()
    }

    suspend fun updateCategory(
        categoryId: Int,
        name: String,
        active: Boolean,
        visible: Boolean
    ) {
        val trimmedName = name.trim()
        require(trimmedName.isNotBlank()) { "Category name cannot be blank" }

        database.withTransaction {
            categoryDao.updateCategoryFields(
                localId = categoryId,
                name = trimmedName,
                active = active,
                visible = visible,
                updatedAt = System.currentTimeMillis()
            )

            outboxDao.insert(
                OutboxOperationEntity(
                    type = OutboxOperationType.UPDATE_CATEGORY,
                    entityType = CATEGORY_ENTITY_TYPE,
                    entityLocalId = categoryId
                )
            )
        }

        syncScheduler.enqueueSync()
    }

    suspend fun deleteCategory(categoryId: Int) {
        database.withTransaction {
            categoryDao.markDeleted(
                localId = categoryId,
                updatedAt = System.currentTimeMillis()
            )

            outboxDao.insert(
                OutboxOperationEntity(
                    type = OutboxOperationType.DELETE_CATEGORY,
                    entityType = CATEGORY_ENTITY_TYPE,
                    entityLocalId = categoryId
                )
            )
        }

        syncScheduler.enqueueSync()
    }

    suspend fun setCategoryVisibility(categoryId: Int, visible: Boolean) {
        database.withTransaction {
            categoryDao.setVisibility(
                localId = categoryId,
                isVisible = visible,
                updatedAt = System.currentTimeMillis()
            )

            outboxDao.insert(
                OutboxOperationEntity(
                    type = OutboxOperationType.UPDATE_CATEGORY,
                    entityType = CATEGORY_ENTITY_TYPE,
                    entityLocalId = categoryId
                )
            )
        }

        syncScheduler.enqueueSync()
    }

    suspend fun getById(id: Int): CategoryEntity? {
        return categoryDao.getByLocalId(id)
    }


//    suspend fun getVisibleCategoriesCreatedBefore(fromTime: Long): List<CategoryEntity> {
//        return categoryDao.getVisibleCategoriesCreatedBefore(fromTime)
//    }

    private companion object {
        const val CATEGORY_ENTITY_TYPE = "Category"
    }
}