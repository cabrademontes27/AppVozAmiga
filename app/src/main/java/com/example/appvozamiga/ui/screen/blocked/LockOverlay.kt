package com.example.appvozamiga.ui.screen.blocked


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LockOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)), // semi-transparente negro
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Modo comandos",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Modo comandos activado",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Toca 4 veces para desbloquear",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
