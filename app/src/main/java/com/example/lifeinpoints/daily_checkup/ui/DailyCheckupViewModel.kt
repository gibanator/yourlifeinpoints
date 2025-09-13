// DailyCheckupViewModel.kt
package com.example.lifeinpoints.daily_checkup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DailyCheckupViewModel : ViewModel() {
    // Приватные MutableStateFlow для хранения состояния
    private val _selectedCategories = MutableStateFlow<Set<Int>>(emptySet())
    private val _isDayEnded = MutableStateFlow(false)
    private val _isMultiplierMode = MutableStateFlow(false) // Режим умножения на 31

    // Публичные StateFlow для наблюдения из UI
    val selectedCategories: StateFlow<Set<Int>> = _selectedCategories.asStateFlow()
    val isDayEnded: StateFlow<Boolean> = _isDayEnded.asStateFlow()
    val isMultiplierMode: StateFlow<Boolean> = _isMultiplierMode.asStateFlow()

    // Метод для переключения состояния категории
    fun toggleCategory(index: Int) {
        viewModelScope.launch {
            val newSelection = _selectedCategories.value.toMutableSet()
            if (newSelection.contains(index)) {
                newSelection.remove(index)
            } else {
                newSelection.add(index)
            }
            _selectedCategories.value = newSelection
        }
    }

    // Метод для переключения состояния завершения дня
    fun toggleDayEnded() {
        viewModelScope.launch {
            _isDayEnded.value = !_isDayEnded.value
        }
    }

    // Метод для переключения режима отображения общего количества
    fun toggleMultiplierMode() {
        viewModelScope.launch {
            _isMultiplierMode.value = !_isMultiplierMode.value
        }
    }

    // Метод для сброса состояния
    fun resetState() {
        viewModelScope.launch {
            _selectedCategories.value = emptySet()
            _isDayEnded.value = false
            _isMultiplierMode.value = false
        }
    }
}