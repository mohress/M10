package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.BrowserScreen
import com.example.ui.components.DashboardScreen
import com.example.ui.components.ProfileCreatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    when (currentScreen) {
                        AppScreen.DASHBOARD -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.CREATOR -> {
                            ProfileCreatorScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        AppScreen.BROWSER -> {
                            BrowserScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
