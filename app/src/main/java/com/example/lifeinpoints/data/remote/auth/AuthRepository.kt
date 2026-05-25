package com.example.lifeinpoints.data.remote.auth

import android.util.Log
import com.example.lifeinpoints.data.remote.api.AuthApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApi: AuthApi
) {
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        trySend(firebaseAuth.currentUser)
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun register(email: String, username: String, password: String) {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).awaitResult()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()
        result.user?.updateProfile(profileUpdates)?.awaitResult()
        syncMe()
    }

    suspend fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
        try {

            syncMe()

        } catch (e: Exception) {

            Log.e("AuthRepository", "Failed to sync user", e)

        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun syncMe() {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("User is not signed in")
        val token = user.getIdToken(false).awaitResult().token
            ?: throw IllegalStateException("Firebase ID token is missing")
        val response = authApi.syncMe("Bearer $token")
        if (!response.isSuccessful) {
            throw IOException("Failed to sync user: HTTP ${response.code()}")
        }
    }
}

private suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        val exception = task.exception
        when {
            task.isSuccessful -> continuation.resume(task.result)
            exception != null -> continuation.resumeWithException(exception)
            else -> continuation.resumeWithException(IllegalStateException("Firebase task failed"))
        }
    }
}
