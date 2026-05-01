package com.example.lifeinpoints.data.remote.auth

import com.example.lifeinpoints.data.remote.api.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    suspend fun register(email: String, password: String) {
        val response = authApi.register(
            RegisterRequest(email, password)
        )

        tokenStorage.saveToken(response.token)
    }
}