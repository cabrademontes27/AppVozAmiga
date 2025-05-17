package com.example.appvozamiga.ui.screen.menu


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appvozamiga.R
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun NoteCommandScreenOverlay() {
    var showDialog by remember { mutableStateOf(false) }

    // Botón arriba a la izquierda
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .offset(y = 32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hambur), // asegúrate de tener este ícono
                contentDescription = "Información de comandos",
                tint = Color.DarkGray
            )
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 👇 Añadimos scroll aquí
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Comandos disponibles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = """
                            Comandos para interactuar con el sistema
                            
                            Medicamentos:
                            Lo que hara sera nombrarte toda la liste de los medicamentos
                            que tengas agregados y su cantidad
                            
                            Foto:
                            Este comando abre de inmediato la camara que te permite 
                            tomar lectura de los nombres que pueda identificar 
                            
                            Lugar 
                            Esperas 1.5 segundos y comenzara a acceder a tu ubicacion,
                            claro, es necesario que le des acceso a tu ubicacion primero
                            si no no podra acceder
                            
                            Codigo:
                            Te redirigira a mostrar tu codigo QR con tus datos personales
                            NO sensibles
                            
                            Compartir:
                            Este comando lo que hara es compartir tu ubicacion actual por SMS 
                            a tus contactos de emergencia 
                            
                            Vinculacion 
                            Este leera tu codigo de vinculacion que la podras ingresar
                            en la pagina web, una tercer persona y te podra monitorear 
                            tanto ubicacion como medicamentos y de control
                        """.trimIndent(),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { showDialog = false }) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
