// com/example/lifeinpoints/notifications/NotificationSettingsScreen.kt
package com.example.lifeinpoints.notifications

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lifeinpoints.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val isEnabled by viewModel.isNotificationEnabled.collectAsState()
    val hour by viewModel.notificationHour.collectAsState()
    val minute by viewModel.notificationMinute.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Информационная карточка
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Daily Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Set a daily reminder to complete your checkup. We'll notify you at the specified time every day to help you build a consistent habit.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Основные настройки
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Включение/выключение уведомлений
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isEnabled)
                                    Icons.Default.NotificationsActive
                                else
                                    Icons.Default.NotificationsOff,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = "Daily reminders",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isEnabled) "Enabled" else "Disabled",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) }
                        )
                    }

                    Divider()

                    // Время уведомления
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = "Reminder time",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = String.format("%02d:%02d", hour, minute),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        TimePickerButton(
                            initialHour = hour,
                            initialMinute = minute,
                            onTimeSelected = { h, m -> viewModel.setNotificationTime(h, m) }
                        )
                    }
                }
            }

            // Советы
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tips for effective reminders:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Set reminders for times when you're usually free\n" +
                                "• Choose consistent times to build habits\n" +
                                "• Evening reminders help you reflect on the day\n" +
                                "• Morning reminders help you plan ahead",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Test Notification",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap the button below to test if notifications are working correctly.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Button(
                                onClick = {
                                    val notificationHelper = NotificationHelper(context)
                                    notificationHelper.showDailyCheckupNotification(
                                        "Test Notification 🧪",
                                        "This is a test notification to check if everything is working!"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Test Notification Now")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerButton(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var currentHour by remember { mutableIntStateOf(initialHour) }
    var currentMinute by remember { mutableIntStateOf(initialMinute) }

    Button(
        onClick = {
            currentHour = initialHour
            currentMinute = initialMinute
            showTimePicker = true
        },
        enabled = true
    ) {
        Text("Change")
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select time") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Часы
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Hour",
                                style = MaterialTheme.typography.labelMedium
                            )
                            HourMinutePicker(
                                value = currentHour,
                                range = 0..23,
                                onValueChange = { currentHour = it }
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Минуты
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Minute",
                                style = MaterialTheme.typography.labelMedium
                            )
                            HourMinutePicker(
                                value = currentMinute,
                                range = 0..59,
                                step = 5,
                                onValueChange = { currentMinute = it }
                            )
                        }
                    }

                    Text(
                        text = "Selected: ${String.format("%02d:%02d", currentHour, currentMinute)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(currentHour, currentMinute)
                        showTimePicker = false
                    }
                ) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HourMinutePicker(
    value: Int,
    range: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Кнопка вверх
        IconButton(
            onClick = {
                val newValue = value + step
                if (newValue <= range.last) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropUp,
                contentDescription = "Increase"
            )
        }

        // Отображаемое значение
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.width(48.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Кнопка вниз
        IconButton(
            onClick = {
                val newValue = value - step
                if (newValue >= range.first) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Decrease"
            )
        }
    }
}