package com.example.lifeinpoints.data.settings

import android.content.Context
import com.example.lifeinpoints.core.ui.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val currentTheme: Flow<ThemeType> = ThemePrefs.getTheme(context)

    suspend fun updateTheme(theme: ThemeType) {
        ThemePrefs.setTheme(context, theme)
    }
}