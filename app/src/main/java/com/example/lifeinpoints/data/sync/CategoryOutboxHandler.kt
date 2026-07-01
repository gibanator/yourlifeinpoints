package com.example.lifeinpoints.data.sync

import com.example.lifeinpoints.data.category.CategoryDao
import com.example.lifeinpoints.data.outbox.OutboxOperationEntity
import com.example.lifeinpoints.data.outbox.OutboxOperationType
import com.example.lifeinpoints.data.remote.api.CategoryApi
import com.example.lifeinpoints.data.remote.auth.AuthTokenProvider
import com.example.lifeinpoints.data.remote.category.CategoryCreateRequest
import com.example.lifeinpoints.data.remote.category.CategoryUpdateRequest
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryOutboxHandler @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryApi: CategoryApi,
    private val auth: AuthTokenProvider
) {
    suspend fun sync(operation: OutboxOperationEntity) {
        when (operation.type) {
            OutboxOperationType.CREATE_CATEGORY -> syncCreate(operation)
            OutboxOperationType.UPDATE_CATEGORY -> syncUpdate(operation)
            OutboxOperationType.DELETE_CATEGORY -> syncDelete(operation)
            else -> throw IOException("Unsupported category operation: ${operation.type}")
        }
    }

    private suspend fun syncCreate(operation: OutboxOperationEntity) {
        val category = categoryDao.getByLocalId(
            operation.requireLocalId()
        ) ?: return

        val token = auth.getAuthorizationHeader()

        val response = categoryApi.createCategory(
            authorization = token,
            request = CategoryCreateRequest(
                name = category.name
            )
        )

        if (!response.isSuccessful) {
            throw IOException("Create category failed: ${response.code()}")
        }

        val serverId = response.body()?.id
            ?: throw IOException("Server returned null id")

        categoryDao.setServerId(
            localId = category.localId,
            serverId = serverId
        )
    }

    private suspend fun syncUpdate(operation: OutboxOperationEntity) {
        val category = categoryDao.getByLocalId(
            operation.requireLocalId()
        ) ?: return

        val token = auth.getAuthorizationHeader()

        val serverId = category.serverId
            ?: return

        val response = categoryApi.updateCategory(
            authorization = token,
            id = serverId,
            request = CategoryUpdateRequest(
                name = category.name,
                active = category.isActive,
                visible = category.isVisible,
            )
        )

        if (!response.isSuccessful) {
            throw IOException("Update category failed: ${response.code()}")
        }
    }

    private suspend fun syncDelete(operation: OutboxOperationEntity) {
        val category = categoryDao.getByLocalId(
            operation.requireLocalId()
        ) ?: return

        val serverId = category.serverId

        if (serverId == null) {
            categoryDao.deleteLocal(category.localId)
            return
        }

        val token = auth.getAuthorizationHeader()

        val response = categoryApi.deleteCategory(
            authorization = token,
            id = serverId
        )

        if (!response.isSuccessful) {
            throw IOException("Failed to delete category: ${response.code()}")
        }

        categoryDao.deleteLocal(category.localId)
    }
}