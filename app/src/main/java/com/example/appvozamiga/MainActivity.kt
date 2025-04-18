package com.example.appvozamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.appvozamiga.ui.navigation.AppNavigation
import com.example.appvozamiga.viewModels.register.RegisterViewModel

class MainActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel.handleStartupIntent(intent, this)

        setContent {
            AppNavigation()
        }
    }
}
