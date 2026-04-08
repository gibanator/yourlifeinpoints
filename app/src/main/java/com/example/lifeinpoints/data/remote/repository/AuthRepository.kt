package com.example.lifeinpoints.data.remote.repository

import com.example.lifeinpoints.data.remote.api.AuthApi
import com.example.lifeinpoints.data.remote.dto.RegisterRequest
import com.example.lifeinpoints.data.remote.dto.UserResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun register(email: String, password: String): UserResponse {
        return authApi.register(RegisterRequest(email, password))
    }
}