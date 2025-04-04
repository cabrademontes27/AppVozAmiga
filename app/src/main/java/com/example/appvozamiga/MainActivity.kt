package com.example.appvozamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appvozamiga.ui.login.Ui.RegisterScreen
import com.example.appvozamiga.ui.login.Ui.RegisterViewModel
import com.example.appvozamiga.ui.login.Ui.SplashScreen
import com.example.appvozamiga.ui.theme.AppVozAmigaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val showSplash = remember { mutableStateOf(true) }

            if (showSplash.value) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    delay(2000) // 2 seconds delay
                    showSplash.value = false
                }
            } else {
                val viewModel: RegisterViewModel by viewModels()
                RegisterScreen(viewModel)
            }
        }
    }
}


