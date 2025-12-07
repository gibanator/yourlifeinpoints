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

    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            repo.updateTheme(theme)
        }
    }
}