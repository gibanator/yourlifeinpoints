package com.example.lifeinpoints.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.core.ui.AppBottomBar
import com.example.lifeinpoints.core.ui.AppTopBar
import com.example.lifeinpoints.core.ui.TopBarController
import com.example.lifeinpoints.core.util.AppNavHost
import com.example.lifeinpoints.core.util.Destination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppWithTopAndBottomBar()
        }
    }
}


@Composable
fun AppWithTopAndBottomBar() {
    val navController = rememberNavController()
    val topBar = remember { TopBarController() }
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        topBar = { AppTopBar(topBar.state, onBack = { navController.popBackStack() }) },
        bottomBar = { AppBottomBar(navController, currentRoute) }
    ) { padding ->
        AppNavHost(
            navController = navController,
            startDestination = Destination.MAIN,
            modifier = Modifier.padding(padding),
            topBar = topBar
        )
    }
}