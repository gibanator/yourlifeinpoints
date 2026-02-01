// com.example.lifeinpoints.data.settings/SettingsPrefs.kt
package com.example.lifeinpoints.data.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.lifeinpoints.core.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_NAME = "settings"

val Context.dataStore by preferencesDataStore(
    name = SETTINGS_NAME
)

object SettingsPrefs {
    private val THEME_KEY = stringPreferencesKey("theme_type")
    private val GAME_MODE_KEY = booleanPreferencesKey("game_mode_enabled")

    // Theme
    fun getTheme(context: Context): Flow<ThemeType> =
        context.dataStore.data.map { prefs ->
            val value = prefs[THEME_KEY]
            when (value) {
                "LIGHT" -> ThemeType.LIGHT
                "DARK" -> ThemeType.DARK
                "SYSTEM", null -> ThemeType.SYSTEM
                else -> ThemeType.SYSTEM
            }
        }

    suspend fun setTheme(context: Context, theme: ThemeType) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }

    // Game Mode
    fun getGameMode(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[GAME_MODE_KEY] ?: true // По умолчанию false
        }

    suspend fun setGameMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[GAME_MODE_KEY] = enabled
        }
    }
}