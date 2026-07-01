package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.ai.AiEvaluateRequest
import com.example.lifeinpoints.data.remote.ai.AiEvaluateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
}
