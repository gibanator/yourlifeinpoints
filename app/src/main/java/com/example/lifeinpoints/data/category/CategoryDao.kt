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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

//    @Query("SELECT * FROM categories")
//    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): CategoryEntity?

    @Query("DELETE FROM categories")
    suspend fun clearAll()

    @Query("SELECT * FROM categories WHERE userId = :userId")
    fun observeByUserId(userId: Int): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE userId = :userId")
    suspend fun getByUserId(userId: Int): List<CategoryEntity>

    @Query("DELETE FROM categories WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}