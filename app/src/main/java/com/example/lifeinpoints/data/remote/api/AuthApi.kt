package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.auth.AuthResponse
import com.example.lifeinpoints.data.remote.auth.RegisterRequest

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest) : AuthResponse

}