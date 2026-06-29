package com.example.lifeinpoints.Settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifeinpoints.R
import com.example.lifeinpoints.core.ui.AppTopAppBar
import com.example.lifeinpoints.core.ui.AutoSizeText
import com.example.lifeinpoints.core.ui.theme.ThemeType
import com.example.lifeinpoints.data.outbox.SyncStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onCategoriesClick: () -> Unit = {},
    onVisibilityClick: () -> Unit = {},
    onTemplatesClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    isLoggedIn: Boolean = false,
    navController: NavController? = null,
    vm: SettingsViewModel
) {
    val currentTheme by vm.currentTheme.collectAsState()
    val gameModeEnabled by vm.gameModeEnabled.collectAsState()
    val syncStatus by vm.syncStatus.collectAsState()

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.theme_setting_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ThemeSelectionCard(
                currentTheme = currentTheme,
                onThemeSelected = { theme -> vm.setTheme(theme) }
            )

            Text(
                text = stringResource(R.string.content_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            CategoriesCard(
                onCategoriesClick = onCategoriesClick,
                onVisibilityClick = onVisibilityClick,
                onTemplatesClick = onTemplatesClick
            )

            Text(
                text = stringResource(R.string.leveling_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LevelSelectionCard(
                isEnabled = gameModeEnabled,
                onToggle = { vm.toggleGameMode() }
            )

            Text(
                text = stringResource(R.string.notification_settings_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            NotificationSettingsCard(
                onNotificationSettingsClick = {
                    navController?.navigate("notification_settings")
                }
            )

            Text(
                text = stringResource(R.string.language_setting_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LanguageSettingsCard()

            Text(
                text = stringResource(R.string.auth_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SyncStatusCard(
                status = syncStatus,
            )

            AuthCard(
                isLoggedIn = isLoggedIn,
                onRegisterClick = onRegisterClick,
                onLoginClick = onLoginClick,
                onLogoutClick = { vm.logout() }
            )

            // bottom spacer so last card isn't flush with navigation bar
            Text(text = "", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AuthCard(
    isLoggedIn: Boolean,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (isLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.auth_logout),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onRegisterClick)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = stringResource(R.string.auth_register),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                VerticalDivider()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onLoginClick)
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = stringResource(R.string.auth_login),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsCard(
    onNotificationSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNotificationSettingsClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.notification_settings_card_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.notification_settings_card_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.cd_notification_settings),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CategoriesCard(
    onCategoriesClick: () -> Unit,
    onVisibilityClick: () -> Unit,
    onTemplatesClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategoriesClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.categories_management_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.categories_management_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVisibilityClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.categories_visibility_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stringResource(R.string.categories_visibility_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTemplatesClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.comment_management_title),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            // 💡 Уменьшите это значение, если строки слишком далеко друг от друга
                            lineHeight = 15.sp
                        ),
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = stringResource(R.string.comment_management_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionCard(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThemeOption(
                title = stringResource(R.string.theme_system_title),
                subtitle = stringResource(R.string.theme_system_subtitle),
                isSelected = currentTheme == ThemeType.SYSTEM,
                onClick = { onThemeSelected(ThemeType.SYSTEM) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ThemeOption(
                title = stringResource(R.string.theme_light_title),
                subtitle = stringResource(R.string.theme_light_subtitle),
                isSelected = currentTheme == ThemeType.LIGHT,
                onClick = { onThemeSelected(ThemeType.LIGHT) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ThemeOption(
                title = stringResource(R.string.theme_dark_title),
                subtitle = stringResource(R.string.theme_dark_subtitle),
                isSelected = currentTheme == ThemeType.DARK,
                onClick = { onThemeSelected(ThemeType.DARK) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ThemeOption(
                title = stringResource(R.string.theme_light_stone_title),
                subtitle = stringResource(R.string.theme_light_stone_subtitle),
                isSelected = currentTheme == ThemeType.LIGHT_STONE,
                onClick = { onThemeSelected(ThemeType.LIGHT_STONE) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ThemeOption(
                title = stringResource(R.string.theme_dark_stone_title),
                subtitle = stringResource(R.string.theme_dark_stone_subtitle),
                isSelected = currentTheme == ThemeType.DARK_STONE,
                onClick = { onThemeSelected(ThemeType.DARK_STONE) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LevelSelectionCard(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        GameModeCard(isEnabled = isEnabled, onToggle = onToggle)
    }
}

@Composable
fun GameModeCard(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.gamemode_tumbler_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.gamemode_tumbler_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isEnabled) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Enabled",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LanguageSettingsCard() {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { openLanguageSettings(context) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.language_setting_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.settings_language_subtitle_system),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SyncStatusCard(
    status: SyncStatus,
    modifier: Modifier = Modifier
) {
    val subtitle = when (status) {
        SyncStatus.Synced -> "Your progress is safe"
        SyncStatus.NotLoggedIn -> "Log in to sync"
        is SyncStatus.Pending -> "${status.count} waiting"
        is SyncStatus.Error -> "Sync unavailable"
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "☁",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    text = "Cloud Sync",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
