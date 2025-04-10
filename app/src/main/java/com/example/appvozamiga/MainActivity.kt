package com.example.appvozamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appvozamiga.ui.Navigation.AppNavigation
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.activity.viewModels
import com.example.appvozamiga.ViewModels.RegisterViewModel
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isRegistered = prefs.getBoolean("is_registered", false)

        // Si aún no está verificado, revisamos si se abrió desde un link
        if (!isRegistered) {
            val data = intent?.data
            val tokenId = data?.getQueryParameter("id")

            if (tokenId != null) {
                registerViewModel.verificarToken(tokenId, context = this)
            }
        }

        setContent {
            AppNavigation()
        }
    }
}