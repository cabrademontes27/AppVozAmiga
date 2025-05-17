package com.example.appvozamiga.ui.screen.menu.functions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.data.models.ControlledMedication
import com.example.appvozamiga.viewModels.menu.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlledDrugsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.loadControlledMedicationsBackend(context)
    }

    val medicamentosControlados = viewModel.controlledMedications

    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    var endDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    var intervalHours by remember { mutableStateOf(8) }

    var medicamentoAEliminar by remember { mutableStateOf<ControlledMedication?>(null) }
    var medicamentoAEditar by remember { mutableStateOf<ControlledMedication?>(null) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                name = ""
                description = ""
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addControlledMedicationBackend(
                            context = context,
                            name = name,
                            description = description,
                            start = startDateTime.timeInMillis,
                            end = endDateTime.timeInMillis,
                            intervalHours = intervalHours
                        )
                        showDialog = false
                        name = ""
                        description = ""
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Agregar medicamento controlado") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, maxLines = 4)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Inicio: ${dateFormatter.format(startDateTime.time)}")
                    Button(onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            TimePickerDialog(context, { _, h, min ->
                                startDateTime.set(y, m, d, h, min)
                            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                    }) { Text("Seleccionar inicio") }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fin: ${dateFormatter.format(endDateTime.time)}")
                    Button(onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            TimePickerDialog(context, { _, h, min ->
                                endDateTime.set(y, m, d, h, min)
                            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                    }) { Text("Seleccionar fin") }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = intervalHours.toString(),
                        onValueChange = { it.toIntOrNull()?.let { intervalHours = it } },
                        label = { Text("Cada cuántas horas tomar") },
                        singleLine = true
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    medicamentoAEliminar?.let { med ->
        AlertDialog(
            onDismissRequest = { medicamentoAEliminar = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteControlledMedicationBackend(context, med)
                    medicamentoAEliminar = null
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoAEliminar = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("¿Eliminar medicamento?") },
            text = { Text("¿Estás seguro de eliminar ${med.name}?") },
            shape = RoundedCornerShape(16.dp)
        )
    }

    medicamentoAEditar?.let { med ->
        var editDesc by remember { mutableStateOf(med.description) }
        AlertDialog(
            onDismissRequest = { medicamentoAEditar = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateControlledMedicationBackend(context, med, editDesc)
                    medicamentoAEditar = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { medicamentoAEditar = null }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Editar: ${med.name}") },
            text = {
                OutlinedTextField(
                    value = editDesc,
                    onValueChange = { editDesc = it },
                    label = { Text("Editar descripción") },
                    maxLines = 4
                )
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Medicamento Controlado")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            TopAppBar(title = { Text("Medicamentos Controlados") })

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                if (medicamentosControlados.isEmpty()) {
                    item {
                        Text("Aún no hay medicamentos controlados registrados.", color = Color.Gray)
                    }
                } else {
                    items(medicamentosControlados) { medicamento ->
                        ControlledMedicationCard(
                            med = medicamento,
                            onDelete = { medicamentoAEliminar = medicamento },
                            onEdit = { medicamentoAEditar = medicamento }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ControlledMedicationCard(
    med: ControlledMedication,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val cardColor = Color(0xFFF3EAF6)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = med.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4A148C)
            )
            Text("Descripción: ${med.description}", color = Color.DarkGray)
            Text("Inicio: ${sdf.format(Date(med.startDateTime))}", color = Color.DarkGray)
            Text("Fin: ${sdf.format(Date(med.endDateTime))}", color = Color.DarkGray)
            Text("Frecuencia: Cada ${med.intervalHours} horas", color = Color.DarkGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Editar", color = Color(0xFF6A1B9A))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("Eliminar", color = Color.Red)
                }
            }
        }
    }
}
