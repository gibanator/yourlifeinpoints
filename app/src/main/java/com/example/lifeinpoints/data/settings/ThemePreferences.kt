package com.example.lifeinpoints.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lifeinpoints.core.ui.theme.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_NAME = "settings"

val Context.dataStore by preferencesDataStore(
    name = SETTINGS_NAME
)

object ThemePrefs {
    private val THEME_KEY = stringPreferencesKey("theme_type")

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
}