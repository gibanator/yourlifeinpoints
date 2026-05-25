package com.example.lifeinpoints.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.data.remote.auth.AuthRepository
import com.example.lifeinpoints.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentTheme: StateFlow<ThemeType> = repo.currentTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeType.SYSTEM
    )

    val gameModeEnabled: StateFlow<Boolean> = repo.gameModeEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val isLoggedIn: StateFlow<Boolean> = authRepository.currentUser
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch { repo.updateTheme(theme) }
    }

    fun toggleGameMode() {
        viewModelScope.launch {
            repo.updateGameMode(!gameModeEnabled.value)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
