package com.example.appvozamiga.ui.screen.menu.functions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import com.example.appvozamiga.viewModels.menu.MainViewModel


@Composable
fun LinkingScreen(viewModel: MainViewModel) {
    val token = remember { mutableStateOf(viewModel.currentToken) }

    // Cada vez que cambia el token (cada 5 minutos), actualizamos el estado
    LaunchedEffect(Unit) {
        viewModel.startTokenGeneration {
            token.value = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tu código de vinculación:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(token.value, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = Color(0xFF2D3436))
        }
    }
}
