package com.example.lifeinpoints.core.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Immutable
data class TopBarState(
    val title: String = "BAR TITLE",
    val showBack: Boolean = false,
    val actions: (@Composable () -> Unit)? = null
)

class TopBarController {
    var state by mutableStateOf(TopBarState()); private set
    fun set(s: TopBarState) {state = s}
    fun clear() {state = TopBarState()}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    state: TopBarState,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(state.title)},
        navigationIcon = {
            if (state.showBack && onBack != null){
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = { state.actions?.invoke() }
    )
}
