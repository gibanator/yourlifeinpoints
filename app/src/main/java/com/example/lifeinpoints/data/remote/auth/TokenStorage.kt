package com.example.lifeinpoints.data.remote.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN = stringPreferencesKey("jwt_token")

    val token: Flow<String?> = dataStore.data.map { it[TOKEN] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN) }
    }
}