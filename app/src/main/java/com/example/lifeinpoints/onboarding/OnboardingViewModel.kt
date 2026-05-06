// com.example.lifeinpoints.onboarding/OnboardingViewModel.kt
package com.example.lifeinpoints.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    private val _themeSelected = MutableStateFlow(false)
    val themeSelected: StateFlow<Boolean> = _themeSelected.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.onboardingCompleted.collect { completed ->
                _onboardingCompleted.value = completed
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            settingsRepository.themeSelected.collect { selected ->
                _themeSelected.value = selected
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
        }
    }

    fun setThemeSelected(theme: ThemeType) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
            settingsRepository.setThemeSelected(true)
        }
    }
}