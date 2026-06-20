package com.example.lifeinpoints.data.remote.api

import com.example.lifeinpoints.data.remote.category.CategoryCreateRequest
import com.example.lifeinpoints.data.remote.category.CategoryCreateResponse
import com.example.lifeinpoints.data.remote.category.CategoryDto
import com.example.lifeinpoints.data.remote.category.CategoryUpdateRequest
import com.example.lifeinpoints.data.remote.category.CategoryVisibilitySwitchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApi {
    @GET("/api/v1/category")
    suspend fun getCategories(
        @Header("Authorization") authorization: String
    ): List<CategoryDto>

    @POST("/api/v1/category")
    suspend fun createCategory(
        @Header("Authorization") authorization: String,
        @Body request: CategoryCreateRequest
    ): Response<CategoryCreateResponse>

    @DELETE("/api/v1/category/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): Response<Unit>

    @PATCH("/api/v1/category/{id}")
    suspend fun updateCategory(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
        @Body request: CategoryUpdateRequest
    ): Response<Unit>

    @PATCH("/api/v1/category/{id}/visibility/switch")
    suspend fun switchCategoryVisibility(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): CategoryVisibilitySwitchResponse
}
