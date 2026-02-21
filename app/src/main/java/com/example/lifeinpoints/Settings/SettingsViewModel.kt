// com.example.lifeinpoints.Settings/SettingsViewModel.kt
package com.example.lifeinpoints.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    // Тема
    val currentTheme: StateFlow<ThemeType> =
        repo.currentTheme.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeType.SYSTEM
        )

    // Game Mode - теперь из DataStore
    val gameModeEnabled: StateFlow<Boolean> =
        repo.gameModeEnabled.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            repo.updateTheme(theme)
        }
    }

    // Функция для переключения Game Mode
    fun toggleGameMode() {
        viewModelScope.launch {
            val current = gameModeEnabled.value
            repo.updateGameMode(!current)
        }
    }

    /*
    // Функция для установки конкретного значения
    fun setGameMode(enabled: Boolean) {
        viewModelScope.launch {
            repo.updateGameMode(enabled)
        }
    }
     */
}