package com.example.lifeinpoints.data.remote.auth

import android.util.Log
import com.example.lifeinpoints.data.remote.api.AuthApi
import com.example.lifeinpoints.data.sync.OutboxSyncer
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authApi: AuthApi,
    private val syncer: OutboxSyncer,
    private val tokenProvider: AuthTokenProvider
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
        syncer.syncOnce()
    }

    suspend fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
        try {
            syncMe()
            syncer.syncOnce()
        } catch (e: Exception) {

            Log.e("AuthRepository", "Failed to sync user", e)

        }
    }

    suspend fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val user = firebaseAuth.signInWithCredential(credential).awaitResult().user
            ?: throw IllegalStateException("Google authentication returned no user")

        val email = user.email?.trim().orEmpty()
        if (email.isBlank()) {
            throw IllegalStateException("Google account did not provide an email address")
        }

        if (user.displayName.isNullOrBlank()) {
            val username = email.substringBefore('@').trim()
            if (username.isBlank()) {
                throw IllegalStateException("Google account did not provide a username")
            }
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            user.updateProfile(profileUpdates).awaitResult()
        }

        syncMe()
        syncer.syncOnce()
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun syncMe() {
        val token = tokenProvider.getAuthorizationHeader()

        val response = authApi.syncMe(token)

        if (!response.isSuccessful) {
            throw IOException("Failed to sync user: HTTP ${response.code()}")
        }
    }
}

suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        val exception = task.exception
        when {
            task.isSuccessful -> continuation.resume(task.result)
            exception != null -> continuation.resumeWithException(exception)
            else -> continuation.resumeWithException(IllegalStateException("Firebase task failed"))
        }
    }
}
