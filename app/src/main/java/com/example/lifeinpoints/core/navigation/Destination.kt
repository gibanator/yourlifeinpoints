package com.example.lifeinpoints.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    MAIN("main","Main", Icons.Default.Home),
    CALENDAR("calendar","Calendar", Icons.Default.DateRange),
    GRAPHS("graphs","Graphs", Icons.Default.Build)
}