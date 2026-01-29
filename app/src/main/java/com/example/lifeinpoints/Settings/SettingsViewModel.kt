package com.example.lifeinpoints.Settings

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: SettingsRepository
) : ViewModel() {

    val currentTheme: StateFlow<ThemeType> =
        repo.currentTheme.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeType.SYSTEM
        )

    // Добавьте состояние для Game Mode
    private val _gameModeEnabled = MutableStateFlow(savedStateHandle.get<Boolean>("game_mode_enabled") ?: false)
    val gameModeEnabled: StateFlow<Boolean> = _gameModeEnabled

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            repo.updateTheme(theme)
        }
    }

    // Функция для переключения Game Mode
    fun toggleGameMode() {
        viewModelScope.launch {
            val newValue = !_gameModeEnabled.value
            _gameModeEnabled.value = newValue
            savedStateHandle.set("game_mode_enabled", newValue)
            // Здесь можно добавить сохранение в репозиторий, если нужно
            // repo.updateGameMode(newValue)
        }
    }
}