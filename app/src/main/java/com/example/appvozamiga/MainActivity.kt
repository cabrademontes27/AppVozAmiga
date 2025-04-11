package com.example.appvozamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.appvozamiga.ui.Navigation.AppNavigation
import com.example.appvozamiga.ViewModels.RegisterViewModel

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
