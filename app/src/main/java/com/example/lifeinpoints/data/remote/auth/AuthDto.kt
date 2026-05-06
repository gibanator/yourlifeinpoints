package com.example.lifeinpoints.data.remote.auth

data class RegisterRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String
)