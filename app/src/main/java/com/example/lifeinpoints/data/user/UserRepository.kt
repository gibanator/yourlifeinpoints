package com.example.lifeinpoints.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val dao: UserDao
) {
    fun observeAll(): Flow<List<UserEntity>> = dao.observeAll()
    suspend fun add(user: UserEntity) = dao.insert(user)
    suspend fun update(user: UserEntity) = dao.update(user)
    suspend fun delete(user: UserEntity) = dao.delete(user)

    suspend fun getById(id: Int): UserEntity? = dao.getById(id)
    suspend fun getByUsername(username: String): UserEntity? = dao.getByUsername(username)
    suspend fun getByEmail(email: String): UserEntity? = dao.getByEmail(email)

    suspend fun getAll(): List<UserEntity> = dao.observeAll().first()
}