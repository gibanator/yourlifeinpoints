package com.example.lifeinpoints.daily_checkup.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTargetViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddTargetUiState())
    val uiState: StateFlow<AddTargetUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onDaysChanged(days: String) {
        _uiState.update { it.copy(daysText = days.filter { c -> c.isDigit() }) }
    }

    fun onPresetDaysSelected(days: Int) {
        _uiState.update { it.copy(daysText = days.toString(), showCustomDaysInput = false) }
    }

    fun onToggleCustomDaysInput() {
        _uiState.update { it.copy(showCustomDaysInput = !it.showCustomDaysInput) }
    }

    fun onDeadlineChanged(deadline: LocalDate?) {
        _uiState.update { it.copy(deadline = deadline) }
    }

    fun showDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun reset() {
        _uiState.update { AddTargetUiState() }
    }
}
