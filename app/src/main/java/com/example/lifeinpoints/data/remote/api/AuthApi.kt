package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.dto.RegisterRequest
import com.example.lifeinpoints.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/users")
    suspend fun register(@Body request: RegisterRequest): UserResponse
}