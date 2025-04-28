package com.example.appvozamiga.ui.screen.menu.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appvozamiga.data.models.Medications
import com.example.appvozamiga.viewModels.menu.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugsScreen(navController: NavController) {
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.checkAndLoadProfile(context)
        viewModel.loadMedicationsLocal(context)
    }
    val medicamentos = viewModel.medicationsUiState.medications

    var showDialog by remember { mutableStateOf(false) }
    var nombreNuevo by remember { mutableStateOf("") }
    var descripcionNueva by remember { mutableStateOf("") }
    var medicamentoSeleccionado by remember { mutableStateOf<Medications?>(null) }
    var descripcionEditada by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Medications?>(null) }

    // Diálogo: Agregar medicamento
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (nombreNuevo.isNotBlank()) {
                        viewModel.addMedicationsBackend(context, nombreNuevo, descripcionNueva)
                    }
                    showDialog = false
                    nombreNuevo = ""
                    descripcionNueva = ""
                }) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Agregar medicamento") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = nombreNuevo,
                        onValueChange = { nombreNuevo = it },
                        label = { Text("Nombre") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcionNueva,
                        onValueChange = { descripcionNueva = it },
                        label = { Text("Descripción") },
                        maxLines = 5
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo: Editar descripción
    if (medicamentoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { medicamentoSeleccionado = null },
            confirmButton = {
                TextButton(onClick = {
                    medicamentoSeleccionado?.let {
                        viewModel.editMedicationsBackend(context, it, descripcionEditada)
                    }
                    medicamentoSeleccionado = null
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoSeleccionado = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text(medicamentoSeleccionado!!.nombre) },
            text = {
                OutlinedTextField(
                    value = descripcionEditada,
                    onValueChange = { descripcionEditada = it },
                    label = { Text("Editar descripción") },
                    maxLines = 3
                )
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Diálogo: Confirmar eliminación
    showDeleteDialog?.let { med ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteMedications(context, med)
                    showDeleteDialog = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("¿Eliminar medicamento?") },
            text = { Text("¿Estás seguro de eliminar ${med.nombre}?") },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Pantalla principal
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Medicamento")
            }
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            TopAppBar(
                title = { Text("Mis Medicamentos") }
            )

            if (medicamentos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no tienes medicamentos registrados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onClick = {
                                medicamentoSeleccionado = medicamento
                                descripcionEditada = medicamento.descripcion
                            },
                            onDelete = {
                                showDeleteDialog = medicamento
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MedicamentoCard(
    medicamento: Medications,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val cardColor = Color(0xFFF9F5F0)
    val iconColor = Color(0xFF4285F4)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = "Ícono medicamento",
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicamento.nombre,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF333333)
                )
                Text(
                    text = medicamento.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}
