package com.example.appvozamiga.ui.screen.menu.functions

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.viewModels.menu.MainViewModel

@Composable
fun SOSScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()

    var statusMessage by remember { mutableStateOf("Cargando contactos de emergencia...") }
    var isSending by remember { mutableStateOf(false) }
    var contactosCargados by remember { mutableStateOf(false) }

    // Forzar carga de contactos desde almacenamiento local
    LaunchedEffect(Unit) {
        viewModel.loadLocalData(context)
        contactosCargados = true
        Log.d("SOS", "📥 Contactos cargados: ${viewModel.emergencyContacts}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emergencia SOS",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = statusMessage,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            color = Color.DarkGray
        )

        Button(
            onClick = {
                isSending = true
                statusMessage = "Obteniendo ubicación..."

                val contactos = viewModel.emergencyContacts.map { it.phone }.filter { it.isNotBlank() }

                Log.d("SOS", "📱 Contactos detectados: $contactos")

                if (contactos.isEmpty()) {
                    statusMessage = "❌ No tienes contactos de emergencia registrados."
                    isSending = false
                    return@Button
                }

                viewModel.getLinkLocation { ubicacion ->
                    val mensaje = "SOS, eres mi contacto de emergencia. Ubicación: $ubicacion"

                    viewModel.sendSmsSOS(contactos, mensaje) { exito ->
                        statusMessage = if (exito) {
                            "✅ Mensaje de SOS enviado correctamente."
                        } else {
                            "❌ Error al enviar el mensaje."
                        }
                        isSending = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            enabled = contactosCargados && !isSending,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("ENVIAR SOS", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver", color = Color.Gray)
        }
    }
}