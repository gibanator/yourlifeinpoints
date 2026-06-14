package com.example.lifeinpoints.daily_checkup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeinpoints.data.target.TargetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditTargetViewModel @Inject constructor(
    private val targetRepository: TargetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTargetUiState())
    val uiState: StateFlow<EditTargetUiState> = _uiState.asStateFlow()

    private var daysObserveJob: Job? = null

    fun initWithTarget(target: TargetUi) {
        _uiState.update {
            EditTargetUiState(
                targetId = target.id,
                name = target.name,
                daysText = target.days.toString(),
                deadline = target.deadline,
                showCustomDaysInput = target.days !in listOf(1, 7, 14, 30)
            )
        }
        daysObserveJob?.cancel()
        daysObserveJob = viewModelScope.launch {
            targetRepository.observeCompletedDaysForTarget(target.id).collect { days ->
                _uiState.update { it.copy(completedDays = days) }
            }
        }
    }

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
}
