package com.example.lifeinpoints.data.remote.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DayCompletionApi {
    @POST("/api/v1/day-completion")
    suspend fun markCompleted(
        @Header("Authorization") authorization: String,
        @Query("date") date: String
    ): Response<Unit>

    @DELETE("/api/v1/day-completion")
    suspend fun unmarkCompleted(
        @Header("Authorization") authorization: String,
        @Query("date") date: String
    ): Response<Unit>
}