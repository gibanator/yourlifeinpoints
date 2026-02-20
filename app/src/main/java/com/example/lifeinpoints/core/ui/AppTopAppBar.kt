// com/example/lifeinpoints/core/ui/AppTopAppBar.kt
package com.example.lifeinpoints.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lifeinpoints.core.ui.theme.LocalThemeType
import com.example.lifeinpoints.core.ui.theme.isStoneTheme

// com/example/lifeinpoints/core/ui/AppTopAppBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
) {
    val isStone = LocalThemeType.current.isStoneTheme

    val finalColors = if (isStone) {
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    } else {
        colors
    }

    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = finalColors
    )
}