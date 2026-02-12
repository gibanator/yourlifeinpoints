// com/example/lifeinpoints/notifications/NotificationSettingsScreen.kt
package com.example.lifeinpoints.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

    val timeText = remember(hour, minute) {
        LocalTime.of(hour, minute).format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(stringResource(R.string.notification_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
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
                        text = stringResource(R.string.notification_daily_reminders_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.notification_daily_reminders_desc),
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
                                    text = stringResource(R.string.notification_daily_reminders_label),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(
                                        if (isEnabled) R.string.enabled else R.string.disabled
                                    ),
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
                                    text = stringResource(R.string.notification_reminder_time_label),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = timeText,
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
                        text = stringResource(R.string.notification_tips_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.notification_tips_body),
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
                                text = stringResource(R.string.notification_test_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.notification_test_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Button(
                                onClick = {
                                    val notificationHelper = NotificationHelper(context)
                                    notificationHelper.showDailyCheckupNotification(
                                        context.getString(R.string.notification_test_notif_title),
                                        context.getString(R.string.notification_test_notif_message)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.notification_test_button))
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
        Text(stringResource(R.string.change))
    }

    if (showTimePicker) {
        val selectedTimeText = remember(currentHour, currentMinute) {
            LocalTime.of(currentHour, currentMinute).format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.select_time)) },
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
                                text = stringResource(R.string.hour),
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
                                text = stringResource(R.string.minute),
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
                        text = stringResource(R.string.selected_time, selectedTimeText),
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
                    Text(stringResource(R.string.set))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
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
                contentDescription = stringResource(R.string.cd_increase)
            )
        }

        // Отображаемое значение
        Text(
            text = "%02d".format(value),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.width(48.dp),
            textAlign = TextAlign.Center
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
                contentDescription = stringResource(R.string.cd_decrease)
            )
        }
    }
}