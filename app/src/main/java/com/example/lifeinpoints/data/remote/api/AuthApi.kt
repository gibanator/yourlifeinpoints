package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.auth.AuthResponse
import com.example.lifeinpoints.data.remote.auth.LoginRequest
import com.example.lifeinpoints.data.remote.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
