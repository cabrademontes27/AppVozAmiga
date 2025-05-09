package com.example.appvozamiga.ui.screen.menu.functions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appvozamiga.viewModels.menu.MainViewModel

@Composable
fun EditProfileDialog(
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(mainViewModel.name.value) }
    var lastName by remember { mutableStateOf(mainViewModel.lastName.value) }
    var secondLastName by remember { mutableStateOf(mainViewModel.secondLastName.value) }
    var birthDay by remember { mutableStateOf(mainViewModel.birthDay.value) }
    var telephone by remember { mutableStateOf(mainViewModel.telephone.value) }
    var street by remember { mutableStateOf(mainViewModel.street.value) }
    var state by remember { mutableStateOf(mainViewModel.state.value) }
    var municipality by remember { mutableStateOf(mainViewModel.municipality.value) }
    var colony by remember { mutableStateOf(mainViewModel.colony.value) }
    var bloodType by remember { mutableStateOf(mainViewModel.bloodType.value) }
    var disabilityDescription by remember { mutableStateOf(mainViewModel.disabilityDescription.value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido Paterno") })
                OutlinedTextField(value = secondLastName, onValueChange = { secondLastName = it }, label = { Text("Apellido Materno") })
                OutlinedTextField(value = birthDay, onValueChange = { birthDay = it }, label = { Text("Fecha de Nacimiento") })
                OutlinedTextField(value = telephone, onValueChange = { telephone = it }, label = { Text("Teléfono") })
                OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Calle") })
                OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("Estado") })
                OutlinedTextField(value = municipality, onValueChange = { municipality = it }, label = { Text("Municipio") })
                OutlinedTextField(value = colony, onValueChange = { colony = it }, label = { Text("Colonia") })
                OutlinedTextField(value = bloodType, onValueChange = { bloodType = it }, label = { Text("Tipo de Sangre") })
                OutlinedTextField(value = disabilityDescription, onValueChange = { disabilityDescription = it }, label = { Text("Descripción de Discapacidad") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                mainViewModel.updateProfileFromDialog(
                    name, lastName, secondLastName, birthDay, telephone,
                    street, state, municipality, colony,
                    bloodType, disabilityDescription
                )
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
