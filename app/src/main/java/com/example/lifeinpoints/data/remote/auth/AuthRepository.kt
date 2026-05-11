package com.example.lifeinpoints.data.remote.auth

import com.example.lifeinpoints.data.remote.api.AuthApi
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {
    suspend fun register(email: String, username: String, password: String) {
        val response = authApi.register(RegisterRequest(email, username, password))
        tokenStorage.saveToken(response.token)
    }

    suspend fun login(email: String, password: String) {
        val response = authApi.login(LoginRequest(email, password))
        tokenStorage.saveToken(response.token)
    }

    suspend fun logout() {
        tokenStorage.clearToken()
    }
}
