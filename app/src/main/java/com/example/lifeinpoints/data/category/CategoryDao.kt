package com.example.lifeinpoints.data.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.Query

@Dao
interface CategoryDao {
    // Существующие методы остаются без изменений
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY isSystem ASC, sortOrder DESC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): CategoryEntity?

    @Query("SELECT * FROM categories ORDER BY isSystem ASC, sortOrder DESC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(:categoryName)")
    suspend fun countByName(categoryName: String): Int

    @Query("DELETE FROM categories WHERE isSystem = 0") // Удаляем только пользовательские
    suspend fun clearUserCategories()

//    @Query("SELECT * FROM categories")
//    suspend fun getAll(): List<CategoryEntity>

//    @Query("SELECT * FROM categories WHERE userId = :userId")
//    fun observeByUserId(userId: Int): Flow<List<CategoryEntity>>
//
//    @Query("SELECT * FROM categories WHERE userId = :userId")
//    suspend fun getByUserId(userId: Int): List<CategoryEntity>
//
//    @Query("DELETE FROM categories WHERE userId = :userId")
//    suspend fun deleteByUserId(userId: Int)
//
//    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId AND LOWER(name) = LOWER(:categoryName)")
//    suspend fun countByUserIdAndName(userId: Int, categoryName: String): Int
//
//    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY sortOrder ASC, name ASC")
//    fun observeByUserIdSorted(userId: Int): Flow<List<CategoryEntity>>
}