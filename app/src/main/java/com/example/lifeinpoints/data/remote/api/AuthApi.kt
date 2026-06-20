package com.example.lifeinpoints.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @GET("/api/v1/me")
    suspend fun syncMe(
        @Header("Authorization") authorization: String
    ): Response<Unit>
}
