package com.example.lifeinpoints.aiScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AiModeUiState(
    val inputText: String = ""
)

@HiltViewModel
class AiModeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AiModeUiState())
    val uiState: StateFlow<AiModeUiState> = _uiState.asStateFlow()

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
}