package com.example.lifeinpoints.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: Long,
    val email: String,
    val createdAt: String?
)