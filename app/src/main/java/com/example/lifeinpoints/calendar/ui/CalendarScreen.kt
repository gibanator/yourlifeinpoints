package com.example.lifeinpoints.calendar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lifeinpoints.core.ui.TopBarController
import com.example.lifeinpoints.core.ui.TopBarState

@Composable
fun CalendarScreen(topBar: TopBarController) {
    LaunchedEffect(Unit) {
        topBar.set(
            TopBarState(
                title = "Calendar",
                actions = {
                    IconButton(onClick = { /* change view */ }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select")
                    }
                }
            )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Calendar Screen")
    }
}