package com.example.appvozamiga.ui.Screen.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun VerificationScreen(onVerificationComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // o cuando completes algo, puede ser al presionar un botón
        onVerificationComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Verificación completada ✅")
    }
}