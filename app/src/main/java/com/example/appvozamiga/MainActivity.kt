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

class MainActivity : ComponentActivity() {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailLink = intent?.data?.toString()
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val savedEmail = prefs.getString("email_for_signin", null)

        if (emailLink != null && FirebaseAuth.getInstance().isSignInWithEmailLink(emailLink) && savedEmail != null) {
            FirebaseAuth.getInstance()
                .signInWithEmailLink(savedEmail, emailLink)
                .addOnSuccessListener {
                    Log.d("MainActivity", "Usuario autenticado correctamente")
                    prefs.edit().putBoolean("is_registered", true).apply()

                    // Aquí notificamos al ViewModel
                    registerViewModel.setUserVerified(context = this)
                }
                .addOnFailureListener {
                    Log.e("MainActivity", "❌ Error de autenticación", it)
                }
        }

        setContent {
            AppNavigation()
        }
    }
}
