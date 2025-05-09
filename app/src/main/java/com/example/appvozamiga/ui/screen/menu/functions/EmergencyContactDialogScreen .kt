package com.example.appvozamiga.ui.screen.menu.functions



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appvozamiga.data.models.EmergencyContact
import com.example.appvozamiga.viewModels.menu.MainViewModel

@Composable
fun EmergencyContactDialogScreen(
    initialContact: EmergencyContact,
    index: Int?,
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialContact.name) }
    var relation by remember { mutableStateOf(initialContact.relation) }
    var phone by remember { mutableStateOf(initialContact.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val updatedContact = EmergencyContact(name, relation, phone)
                if (index != null) {
                    mainViewModel.updateEmergencyContact(index, updatedContact)
                } else {
                    mainViewModel.addEmergencyContact(updatedContact)
                }
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text(if (index != null) "Editar Contacto" else "Agregar Contacto") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text("Relación") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
