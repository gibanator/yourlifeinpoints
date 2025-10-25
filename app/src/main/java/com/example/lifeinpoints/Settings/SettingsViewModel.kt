package com.example.lifeinpoints.Settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.lifeinpoints.core.ui.theme.ThemeType
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentTheme = MutableStateFlow(ThemeType.SYSTEM)
    val currentTheme: StateFlow<ThemeType> = _currentTheme

    init {
        loadSavedTheme()
    }

    fun setTheme(themeType: ThemeType) {
        _currentTheme.value = themeType
        // Сохраняем в SavedStateHandle для сохранения при смене конфигурации
        savedStateHandle["currentTheme"] = themeType.name
    }

    private fun loadSavedTheme() {
        val savedTheme = savedStateHandle.get<String>("currentTheme")
        savedTheme?.let {
            _currentTheme.value = ThemeType.valueOf(it)
        }
    }
}