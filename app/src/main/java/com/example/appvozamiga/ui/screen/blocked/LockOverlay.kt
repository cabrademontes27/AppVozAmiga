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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appvozamiga.viewModels.menu.MainViewModel


@Composable
fun LockOverlay() {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val textoReconocido = viewModel.recognizedTextVoice

    LaunchedEffect(textoReconocido) {
        android.util.Log.d("UI", "📋 Mostrando en pantalla: $textoReconocido")
    }

    val micPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        micPermissionGranted.value = granted
        if (!granted) {
            Toast.makeText(context, "Se necesita permiso de micrófono para comandos por voz", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!micPermissionGranted.value) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
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

            // 👉 Aquí agregas la transcripción en vivo
            if (textoReconocido.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = textoReconocido,
                    color = Color.Yellow,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

    }
}
