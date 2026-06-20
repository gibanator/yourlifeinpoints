package com.example.lifeinpoints.data.category

import android.content.Context
import android.util.Log
import com.example.lifeinpoints.R
import com.example.lifeinpoints.data.remote.api.CategoryApi
import com.example.lifeinpoints.data.remote.category.CategoryCreateRequest
import com.example.lifeinpoints.data.remote.category.CategoryDto
import com.example.lifeinpoints.data.remote.category.CategoryUpdateRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryApi: CategoryApi,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val categories = MutableStateFlow<List<CategoryDto>>(emptyList())

    suspend fun initializeSystemCategories() {
        refreshCategoriesIfSignedIn()
    }

    suspend fun addCategory(name: String, createdAt: Long): Result<Unit> {
        return runCatching {
            val trimmedName = name.trim()
            val response = categoryApi.createCategory(
                authorization = authHeader(),
                request = CategoryCreateRequest(name = trimmedName)
            )
            ensureSuccessful(
                code = response.code(),
                isSuccessful = response.isSuccessful,
                message = context.getString(R.string.error_category_create_failed_http, response.code())
            )

            val createdId = response.body()?.id?.takeIf { it > 0 }
                ?: throw IOException(context.getString(R.string.error_category_create_failed))

            categories.value = sortCategories(
                categories.value + CategoryDto(
                    id = createdId,
                    name = trimmedName,
                    active = true,
                    visible = true
                )
            )
        }
    }

    suspend fun updateCategory(
        categoryId: Int,
        name: String,
        active: Boolean,
        visible: Boolean
    ): Result<Unit> {
        return runCatching {
            val trimmedName = name.trim()
            val response = categoryApi.updateCategory(
                authorization = authHeader(),
                id = categoryId,
                request = CategoryUpdateRequest(
                    name = trimmedName,
                    active = active,
                    visible = visible
                )
            )
            ensureSuccessful(
                code = response.code(),
                isSuccessful = response.isSuccessful,
                message = context.getString(R.string.error_category_update_failed_http, response.code())
            )

            categories.value = categories.value.map { category ->
                if (category.id == categoryId) {
                    category.copy(name = trimmedName, active = active, visible = visible)
                } else {
                    category
                }
            }
        }
    }

    suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        return runCatching {
            val response = categoryApi.deleteCategory(
                authorization = authHeader(),
                id = categoryId
            )
            Log.d("CategoryDebug", "delete id = $categoryId")

            Log.d("CategoryDebug", "delete code = ${response.code()}")

            Log.d("CategoryDebug", "delete successful = ${response.isSuccessful}")

            Log.d("CategoryDebug", "delete error = ${response.errorBody()?.string()}")
            ensureSuccessful(
                code = response.code(),
                isSuccessful = response.isSuccessful,
                message = context.getString(R.string.error_category_delete_failed_http, response.code())
            )

            categories.value = categories.value.filterNot { it.id == categoryId }
        }
    }

    suspend fun switchCategoryVisibility(categoryId: Int): Result<Boolean> {
        return runCatching {
            val response = categoryApi.switchCategoryVisibility(
                authorization = authHeader(),
                id = categoryId
            )
            val responseId = response.id?.takeIf { it > 0 } ?: categoryId
            categories.value = categories.value.map { category ->
                if (category.id == responseId) {
                    category.copy(visible = response.visible)
                } else {
                    category
                }
            }
            response.visible
        }
    }

    suspend fun getById(id: Int): CategoryDto? {
        return categories.value.firstOrNull { it.id == id }
            ?: run {
                refreshCategoriesIfSignedIn()
                categories.value.firstOrNull { it.id == id }
            }
    }

    suspend fun getAll(): List<CategoryDto> {
        refreshCategoriesIfSignedIn()
        return sortCategories(categories.value)
    }

    suspend fun getVisibleCategoriesCreatedBefore(fromTime: Long): List<CategoryDto> {
        refreshCategoriesIfSignedIn()
        return sortCategories(categories.value.filter { it.active && it.visible })
    }

    private suspend fun refreshCategoriesIfSignedIn() {
        val authorization = authHeaderOrNull()
        if (authorization == null) {
            categories.value = emptyList()
            return
        }
        val remoteCategories = categoryApi.getCategories(authorization)
        remoteCategories.forEach {
            Log.d("Category", "${it.id} ${it.name} active=${it.active} visible=${it.visible}")
        }
        categories.value = sortCategories(
            remoteCategories.filter { category ->
                category.id != null
            }
        )
    }

    private suspend fun authHeader(): String {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException(context.getString(R.string.error_user_not_signed_in))
        val token = user.getIdToken(false).awaitResult().token
            ?: throw IllegalStateException(context.getString(R.string.error_firebase_token_missing))
        return "Bearer $token"
    }

    private suspend fun authHeaderOrNull(): String? {
        val user = firebaseAuth.currentUser ?: return null
        val token = user.getIdToken(false).awaitResult().token ?: return null
        return "Bearer $token"
    }

    private fun sortCategories(categories: List<CategoryDto>): List<CategoryDto> {
        return categories.sortedBy { it.id ?: Int.MAX_VALUE }
    }

    private fun ensureSuccessful(code: Int, isSuccessful: Boolean, message: String) {
        if (!isSuccessful) {
            throw IOException(message)
        }
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        val exception = task.exception
        when {
            task.isSuccessful -> continuation.resume(task.result)
            exception != null -> continuation.resumeWithException(exception)
            else -> continuation.resumeWithException(IllegalStateException("Firebase task failed"))
        }
    }
}
