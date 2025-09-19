package com.example.lifeinpoints.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.example.lifeinpoints.core.navigation.AppNavHost
import com.example.lifeinpoints.core.ui.AppBottomBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppWithTopAndBottomBar()
        }
    }
}

@Preview
@Composable
fun AppWithTopAndBottomBar() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                end   = padding.calculateEndPadding(LayoutDirection.Ltr),
                bottom= padding.calculateBottomPadding()
            )
        )
    }
}