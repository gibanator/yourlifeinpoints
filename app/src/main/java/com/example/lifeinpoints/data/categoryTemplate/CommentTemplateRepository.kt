package com.example.lifeinpoints.data.categoryTemplate

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentTemplateRepository @Inject constructor(
    private val dao: CommentTemplateDao
) {

    fun observeAll(): Flow<List<CommentTemplateEntity>> = dao.observeAll()
    fun observeByCategory(categoryId: Int): Flow<List<CommentTemplateEntity>> =
        dao.observeByCategory(categoryId)

    suspend fun getByCategory(categoryId: Int): List<CommentTemplateEntity> =
        dao.getByCategory(categoryId)

    suspend fun upsertTemplate(
        categoryId: Int,
        position: Int,
        text: String
    ) {
        require(position in 0..4) { "Template position must be 0..4" }

        val trimmed = text.trim()
        if (trimmed.isBlank()) {
            dao.clearSlot(categoryId, position)
        } else {
            dao.upsert(
                CommentTemplateEntity(
                    categoryLocalId = categoryId,
                    position = position,
                    text = trimmed
                )
            )
        }
    }

    suspend fun clearSlot(categoryId: Int, position: Int) {
        require(position in 0..4) { "Template position must be 0..4" }
        dao.clearSlot(categoryId, position)
    }

}
