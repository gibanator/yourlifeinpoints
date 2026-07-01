package com.example.lifeinpoints.data.remote.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenProvider @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    suspend fun getAuthorizationHeader(): String {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException("User is not signed in")

        val token = user.getIdToken(false).awaitResult().token
            ?: throw IllegalStateException("Firebase ID token is missing")

        return "Bearer $token"
    }
}