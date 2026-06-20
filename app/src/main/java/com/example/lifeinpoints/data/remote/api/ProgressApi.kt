package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.progress.ProgressDayRequest
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ProgressApi {
    @POST("/api/v1/progress")
    suspend fun saveProgress(
        @Header("Authorization") authorization: String,
        @Body request: ProgressDayRequest
    ): Response<Unit>

    @GET("/api/v1/progress")
    suspend fun getProgress(
        @Header("Authorization") authorization: String,
        @Query("date") date: String
    ): Response<JsonElement>
}
