package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.lifeinpoints.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTargetSheet(
    onDismiss: () -> Unit,
    onConfirm: (name: String, days: Int, deadline: LocalDate?) -> Unit,
    vm: AddTargetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val hPad = maxWidth * 0.05f
        val vGap = maxWidth * 0.04f
        val chipGap = vGap * 0.5f

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPad, vertical = vGap),
            verticalArrangement = Arrangement.spacedBy(vGap)
        ) {
            Text(
                text = stringResource(R.string.add_target_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = uiState.name,
                onValueChange = vm::onNameChanged,
                label = { Text(stringResource(R.string.target_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- Блок выбора количества дней ---
            Column(verticalArrangement = Arrangement.spacedBy(chipGap)) {
                Text(
                    text = stringResource(R.string.target_days_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(chipGap)
                ) {
                    listOf(1, 7, 14, 30).forEach { preset ->
                        DaysChip(
                            label = "$preset",
                            isSelected = uiState.daysText == preset.toString() && !uiState.showCustomDaysInput,
                            onClick = { vm.onPresetDaysSelected(preset) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    DaysChip(
                        label = "...",
                        isSelected = uiState.showCustomDaysInput,
                        onClick = { vm.onToggleCustomDaysInput() },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (uiState.showCustomDaysInput) {
                    OutlinedTextField(
                        value = uiState.daysText,
                        onValueChange = vm::onDaysChanged,
                        label = { Text(stringResource(R.string.target_days_input_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // --- Дедлайн ---
            Box {
                OutlinedTextField(
                    value = uiState.deadline?.format(dateFormatter) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.target_deadline_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(modifier = Modifier.matchParentSize().clickable { vm.showDatePicker() })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(vGap)
            ) {
                OutlinedButton(
                    onClick = {
                        vm.reset()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.cancel)) }
                Button(
                    onClick = {
                        val days = uiState.daysText.toIntOrNull() ?: return@Button
                        if (uiState.name.isNotBlank() && days > 0) {
                            onConfirm(uiState.name.trim(), days, uiState.deadline)
                            vm.reset()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.add_button)) }
            }
        }
    }

    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { vm.hideDatePicker() },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        vm.onDeadlineChanged(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                    vm.hideDatePicker()
                }) { Text(stringResource(R.string.ok_button)) }
            },
            dismissButton = {
                TextButton(onClick = { vm.hideDatePicker() }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DaysChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                             else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val vertPad = maxWidth * 0.15f
            val fontSize = (maxWidth.value * 0.22f).sp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = vertPad)
            ) {
                Text(
                    text = label,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
