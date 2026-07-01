package com.example.lifeinpoints.data.categoryTemplate

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface CommentTemplateDao {

    // --- Read ---

    @Query("""
    SELECT * FROM comment_templates
    ORDER BY categoryLocalId ASC, position ASC
    """
    )
    fun observeAll(): Flow<List<CommentTemplateEntity>>

    @Query(
        """
        SELECT * FROM comment_templates
        WHERE categoryLocalId = :categoryId
        ORDER BY position ASC
    """
    )
    fun observeByCategory(categoryId: Int): Flow<List<CommentTemplateEntity>>

    @Query(
        """
        SELECT * FROM comment_templates
        WHERE categoryLocalId = :categoryId
        ORDER BY position ASC
    """
    )
    suspend fun getByCategory(categoryId: Int): List<CommentTemplateEntity>

    @Query(
        """
        SELECT * FROM comment_templates
        WHERE categoryLocalId = :categoryId AND position = :position
        LIMIT 1
    """
    )
    suspend fun getSlot(categoryId: Int, position: Int): CommentTemplateEntity?

    // --- Write ---

    @Upsert
    suspend fun upsert(template: CommentTemplateEntity)

    @Upsert
    suspend fun upsertAll(templates: List<CommentTemplateEntity>)

    @Query(
        """
        DELETE FROM comment_templates
        WHERE categoryLocalId = :categoryId AND position = :position
    """
    )
    suspend fun clearSlot(categoryId: Int, position: Int)

    @Query(
        """
        DELETE FROM comment_templates
        WHERE categoryLocalId = :categoryId
    """
    )
    suspend fun deleteByCategory(categoryId: Int)

    // --- Optional: for "reorder" support later ---

    @Transaction
    suspend fun replaceAllForCategory(categoryId: Int, newTemplates: List<CommentTemplateEntity>) {
        deleteByCategory(categoryId)
        upsertAll(newTemplates)
    }
}
