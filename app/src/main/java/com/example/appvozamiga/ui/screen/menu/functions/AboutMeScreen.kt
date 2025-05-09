package com.example.appvozamiga.ui.screen.menu.functions

import android.app.Application
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.viewModels.menu.MainViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.material.icons.filled.Delete
import com.example.appvozamiga.data.models.EmergencyContact


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutMeScreen(navController: NavController) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedContactIndex by remember { mutableStateOf(-1) }
    var showEditContactDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory {
        initializer { MainViewModel(context.applicationContext as Application) }
    })

    LaunchedEffect(Unit) {
        mainViewModel.checkAndLoadProfile(context)
    }

    val name = mainViewModel.name.value
    val lastName = mainViewModel.lastName.value
    val secondLastName = mainViewModel.secondLastName.value
    val birthDay = mainViewModel.birthDay.value
    val email = mainViewModel.email.value
    val telephone = mainViewModel.telephone.value
    val state = mainViewModel.state.value
    val municipality = mainViewModel.municipality.value
    val colony = mainViewModel.colony.value
    val street = mainViewModel.street.value

    val emergencyContacts = mainViewModel.emergencyContacts

    val PrimaryColor = Color(0xFFA8957D)
    val CardColor = Color(0xFFD5CABA)
    val AccentText = Color(0xFF5D4A3A)
    val SubtleText = Color(0xFF888888)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mi Perfil", color = AccentText)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = AccentText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = CardColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = AccentText)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${name.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase(),
                            fontSize = 36.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$name $lastName $secondLastName".trim(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = email, fontSize = 14.sp, color = SubtleText)
                }
            }

            ProfileSection("Información Personal", AccentText, SubtleText) {
                ProfileField("Fecha de Nacimiento", birthDay, SubtleText, AccentText)
                ProfileField("Correo Electrónico", email, SubtleText, AccentText)
                ProfileField("Teléfono", telephone, SubtleText, AccentText)
            }

            ProfileSection("Dirección", AccentText, SubtleText) {
                ProfileField("Estado", state, SubtleText, AccentText)
                ProfileField("Municipio", municipality, SubtleText, AccentText)
                ProfileField("Colonia", colony, SubtleText, AccentText)
                ProfileField("Calle", street, SubtleText, AccentText)
            }

            Text("Contactos de Emergencia", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AccentText, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

            if (emergencyContacts.size < 3) {
                OutlinedButton(
                    onClick = {
                        selectedContactIndex = -1
                        showEditContactDialog = true
                    },
                    enabled = emergencyContacts.size < 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Agregar contacto")
                }
            }

            emergencyContacts.forEachIndexed { index, contact ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(contact.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = AccentText)
                                Text("Relación: ${contact.relation}", fontSize = 14.sp, color = SubtleText)
                                Text("Teléfono: ${contact.phone}", fontSize = 14.sp, color = PrimaryColor)
                            }
                            Row {
                                IconButton(onClick = {
                                    selectedContactIndex = index
                                    showEditContactDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar contacto", tint = AccentText)
                                }
                                IconButton(onClick = {
                                    mainViewModel.deleteEmergencyContact(index)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar contacto", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Eliminar Cuenta", color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showEditDialog) {
            EditProfileDialog(mainViewModel = mainViewModel, onDismiss = { showEditDialog = false })
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        mainViewModel.deleteAccount(context)
                        navController.navigate("Register") {
                            popUpTo("AboutMe") { inclusive = true }
                        }
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showEditContactDialog) {
            EmergencyContactDialogScreen(
                initialContact = if (selectedContactIndex != -1) emergencyContacts[selectedContactIndex] else EmergencyContact(),
                index = if (selectedContactIndex != -1) selectedContactIndex else null,
                mainViewModel = mainViewModel,
                onDismiss = { showEditContactDialog = false }
            )
        }

    }
}

@Composable
private fun ProfileSection(title: String, accent: Color, subtle: Color, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = accent, modifier = Modifier.padding(bottom = 8.dp))
        content()
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = subtle.copy(alpha = 0.3f))
    }
}

@Composable
private fun ProfileField(label: String, value: String, subtle: Color, accent: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 14.sp, color = subtle)
        Text(if (value.isNotEmpty()) value else "No especificado", fontSize = 16.sp, color = accent, modifier = Modifier.padding(top = 2.dp))
    }
}

