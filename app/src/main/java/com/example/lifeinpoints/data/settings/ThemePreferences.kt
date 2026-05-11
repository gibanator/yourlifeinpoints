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
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")

    private val THEME_SELECTED_KEY = booleanPreferencesKey("theme_selected")
    // Theme
    // com/example/lifeinpoints/data/settings/ThemePrefs.kt
    fun getTheme(context: Context): Flow<ThemeType> =
        context.dataStore.data.map { prefs ->
            val value = prefs[THEME_KEY]
            when (value) {
                "LIGHT" -> ThemeType.LIGHT
                "DARK" -> ThemeType.DARK
                "DARK_STONE" -> ThemeType.DARK_STONE
                "LIGHT_STONE" -> ThemeType.LIGHT_STONE
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

    // Onboarding
    fun getOnboardingCompleted(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] ?: false
        }

    suspend fun setOnboardingCompleted(context: Context, completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    // Флаг, что тема уже была выбрана
    fun getThemeSelected(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[THEME_SELECTED_KEY] ?: false
        }

    suspend fun setThemeSelected(context: Context, selected: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[THEME_SELECTED_KEY] = selected
        }
    }
}