package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.AppScreen
import com.example.ui.AlnauraViewModel
import com.example.ui.screens.AlnauraHomeScreen
import com.example.ui.screens.AlnauraQuizPlayScreen
import com.example.ui.screens.AlnauraResultScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AlnauraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                AnimatedContent(
                    targetState = uiState.currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        AppScreen.HOME, AppScreen.STICKER_BOOK, AppScreen.CUSTOM_QUIZ_LIST -> {
                            AlnauraHomeScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        AppScreen.PLAYING -> {
                            AlnauraQuizPlayScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        AppScreen.RESULT -> {
                            AlnauraResultScreen(
                                uiState = uiState,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}


