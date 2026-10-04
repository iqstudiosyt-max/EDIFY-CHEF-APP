package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChangePasswordScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.theme.AppBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.KitchenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    val viewModel: KitchenViewModel = viewModel()
                    KitchenApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun KitchenApp(viewModel: KitchenViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            CurrentScreen.LOGIN -> LoginScreen(viewModel = viewModel)
            CurrentScreen.MAIN -> MainScreen(viewModel = viewModel)
            CurrentScreen.EDIT_PROFILE -> EditProfileScreen(viewModel = viewModel)
            CurrentScreen.CHANGE_PASSWORD -> ChangePasswordScreen(viewModel = viewModel)
        }
    }
}
