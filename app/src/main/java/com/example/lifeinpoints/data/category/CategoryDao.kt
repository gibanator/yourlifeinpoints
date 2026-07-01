package com.example.lifeinpoints.data.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE localId = :localId")
    suspend fun getByLocalId(localId: Int): CategoryEntity?

    @Query("SELECT * FROM categories WHERE localId IN (:localIds)")
    suspend fun getByLocalIds(localIds: List<Int>): List<CategoryEntity>

    @Query("""
        SELECT * FROM categories 
        WHERE isDeleted = 0 
        ORDER BY isSystem ASC, sortOrder DESC
    """)
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("""
        SELECT * FROM categories 
        WHERE isVisible = 1 AND isDeleted = 0 
        ORDER BY isSystem ASC, sortOrder DESC
    """)
    fun observeVisibleCategories(): Flow<List<CategoryEntity>>

    @Query("""
    SELECT *
    FROM categories
    WHERE isVisible = 1
      AND isDeleted = 0
      AND createdAt <= :fromTime
    ORDER BY isSystem ASC, sortOrder DESC
    """)
    fun observeVisibleCategoriesCreatedBefore(
        fromTime: Long
    ): Flow<List<CategoryEntity>>

    @Query("""
        UPDATE categories 
        SET name = :name, updatedAt = :updatedAt 
        WHERE localId = :localId
    """)
    suspend fun updateName(
        localId: Int,
        name: String,
        updatedAt: Long = System.currentTimeMillis()
    )



    @Query("""
    UPDATE categories
    SET isVisible = :isVisible,
        updatedAt = :updatedAt
    WHERE localId = :localId
    """)
    suspend fun setVisibility(
        localId: Int,
        isVisible: Boolean,
        updatedAt: Long
    )

    @Query("""
        UPDATE categories 
        SET isDeleted = 1, isActive = 0, updatedAt = :updatedAt 
        WHERE localId = :localId
    """)
    suspend fun markDeleted(
        localId: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE categories 
        SET serverId = :serverId, updatedAt = :updatedAt 
        WHERE localId = :localId
    """)
    suspend fun setServerId(
        localId: Int,
        serverId: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
    UPDATE categories
    SET name = :name,
        isActive = :active,
        isVisible = :visible,
        updatedAt = :updatedAt
    WHERE localId = :localId
    """)
    suspend fun updateCategoryFields(
        localId: Int,
        name: String,
        active: Boolean,
        visible: Boolean,
        updatedAt: Long
    )

    @Query("DELETE FROM categories WHERE localId = :localId")
    suspend fun deleteLocal(localId: Int)
}