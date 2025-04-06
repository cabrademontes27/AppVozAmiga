package com.example.appvozamiga.ui.Screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun LoadingScreen() {
    LaunchedEffect(Unit) {
        delay(1000) // Espera 2 segundos y luego navega
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA4937F)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator( color = Color.White,
            strokeWidth = 6.dp,
            modifier = Modifier.size(60.dp))
    }
}