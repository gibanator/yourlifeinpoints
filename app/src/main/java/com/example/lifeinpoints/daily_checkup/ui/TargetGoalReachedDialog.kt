package com.example.lifeinpoints.daily_checkup.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TargetGoalReachedDialog(
    target: TargetUi,
    remainingCount: Int,
    completedTargetsCount: Int,
    onComplete: () -> Unit,
    onExtend: (Int) -> Unit,
    onDismiss: () -> Unit,
    onViewCompleted: () -> Unit
) {
    var additionalDaysText by remember(target.id) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Цель выполнена!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val gap = maxWidth * 0.022f
                Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                    Text(
                        text = target.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Выполнено: ${target.daysSelected} из ${target.days} дней",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (remainingCount > 1) {
                        Text(
                            text = "Ещё ${remainingCount - 1} выполненных",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Если вы ещё не закончили с целью, можете добавить себе дней.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = additionalDaysText,
                        onValueChange = { additionalDaysText = it.filter { c -> c.isDigit() } },
                        label = { Text("Добавить дней") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (completedTargetsCount > 0) {
                        TextButton(
                            onClick = onViewCompleted,
                            contentPadding = PaddingValues()
                        ) {
                            Text(
                                text = "Завершённых целей: $completedTargetsCount",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Позже") }
        },
        confirmButton = {
            BoxWithConstraints {
                val gap = maxWidth * 0.022f
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    OutlinedButton(onClick = onComplete) { Text("Завершить") }
                    Button(
                        onClick = {
                            val days = additionalDaysText.toIntOrNull()
                            if (days != null && days > 0) onExtend(days)
                        },
                        enabled = additionalDaysText.toIntOrNull()?.let { it > 0 } == true
                    ) { Text("Добавить") }
                }
            }
        }
    )
}
