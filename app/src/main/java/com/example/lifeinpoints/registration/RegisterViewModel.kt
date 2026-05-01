package com.example.lifeinpoints.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.remote.auth.AuthRepository
import com.example.lifeinpoints.data.remote.auth.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegisterEvent {
    data object Success : RegisterEvent
}

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterEvent>()
    val events = _events.asSharedFlow()

    val token = tokenStorage.token

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun register() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email and password are required")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            try {
                repository.register(
                    email = state.email.trim(),
                    password = state.password
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                    )
                }

                _events.emit(RegisterEvent.Success)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Registration failed"
                    )
                }
            }
        }
    }
}