// com.example.lifeinpoints.data.settings/SettingsRepository.kt
package com.example.lifeinpoints.data.settings

import android.content.Context
import com.example.lifeinpoints.core.ui.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Тема
    val currentTheme: Flow<ThemeType> = SettingsPrefs.getTheme(context)

    // Game Mode
    val gameModeEnabled: Flow<Boolean> = SettingsPrefs.getGameMode(context)

    val onboardingCompleted: Flow<Boolean> = SettingsPrefs.getOnboardingCompleted(context)

    val themeSelected: Flow<Boolean> = SettingsPrefs.getThemeSelected(context)

    suspend fun updateTheme(theme: ThemeType) {
        SettingsPrefs.setTheme(context, theme)
    }

    suspend fun updateGameMode(enabled: Boolean) {
        SettingsPrefs.setGameMode(context, enabled)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        SettingsPrefs.setOnboardingCompleted(context, completed)
    }

    suspend fun setThemeSelected(selected: Boolean) {
        SettingsPrefs.setThemeSelected(context, selected)
    }
}