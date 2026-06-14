package com.example.lifeinpoints.daily_checkup.ui

import java.time.LocalDate

data class EditTargetUiState(
    val targetId: Int = -1,
    val name: String = "",
    val daysText: String = "",
    val deadline: LocalDate? = null,
    val showDatePicker: Boolean = false,
    val showCustomDaysInput: Boolean = false,
    val completedDays: List<String> = emptyList()
)
