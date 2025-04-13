package com.example.appvozamiga.ui.screen.menu.functions

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.appvozamiga.data.models.UserData
import com.example.appvozamiga.data.models.Location as UserLocation
import com.example.appvozamiga.viewModels.menu.MainViewModel

@Composable
fun EditProfileDialog(
    context: Context,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(viewModel.name.value) }
    var lastName by remember { mutableStateOf(viewModel.lastName.value) }
    var telephone by remember { mutableStateOf(viewModel.telephone.value) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Editar Perfil", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellido") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telephone,
                    onValueChange = { telephone = it },
                    label = { Text("Teléfono") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.name.value = name
                        viewModel.lastName.value = lastName
                        viewModel.telephone.value = telephone

                        val user = UserData(
                            name = name,
                            lastName = lastName,
                            secondLastName = viewModel.secondLastName.value,
                            email = viewModel.email.value,
                            telephone = telephone,
                            birthDay = viewModel.birthDay.value,
                            location = UserLocation(
                                state = viewModel.state.value,
                                municipality = viewModel.municipality.value,
                                colony = viewModel.colony.value,
                                street = viewModel.street.value
                            )
                        )
                        viewModel.actualizarPerfil(context, user)
                        onDismiss()
                    }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
