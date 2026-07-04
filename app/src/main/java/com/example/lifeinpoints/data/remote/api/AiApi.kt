package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.ai.AiEvaluateRequest
import com.example.lifeinpoints.data.remote.ai.AiEvaluateResponse
import com.example.lifeinpoints.data.remote.ai.TranscriptionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AiApi {
    @POST("/api/v1/ai/evaluate")
    suspend fun evaluateDay(
        @Header("Authorization") authorization: String,
        @Body request: AiEvaluateRequest
    ): Response<AiEvaluateResponse>

    /** Список доступных провайдеров, например ["GIGACHAT"]. */
    @GET("/api/v1/ai/models")
    suspend fun getModels(
        @Header("Authorization") authorization: String
    ): Response<List<String>>

    @Multipart
    @POST("/api/v1/audio/transcribe")
    suspend fun transcribe(
        @Part file: MultipartBody.Part
    ): Response<TranscriptionResponse>
}
