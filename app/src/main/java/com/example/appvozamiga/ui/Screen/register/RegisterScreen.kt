package com.example.appvozamiga.ui.Screen.register

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appvozamiga.R
import androidx.compose.ui.platform.LocalContext
import com.example.appvozamiga.ViewModels.RegisterViewModel
import com.example.appvozamiga.repository.fireBase.setUserRegistered


@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onRegisterComplete: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Register(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            viewModel = viewModel,
            onRegisterComplete = onRegisterComplete
        )
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun Register(
    modifier: Modifier,
    viewModel: RegisterViewModel,
    onRegisterComplete: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState
    //var isLoading = mutableStateOf(false)
    //var isSuccess = mutableStateOf(false)



    val name by viewModel.name.observeAsState("")
    val lastName by viewModel.lastName.observeAsState("")
    val secondLastName by viewModel.secondLastName.observeAsState("")
    val telephone by viewModel.telephone.observeAsState("")
    val isEnabled by viewModel.registerEnable.observeAsState(false)
    val birthDay by viewModel.birthDay.observeAsState("")
    val state by viewModel.state.observeAsState("")
    val municipality by viewModel.municipality.observeAsState("")
    val colony by viewModel.colony.observeAsState("")
    val street by viewModel.street.observeAsState("")
    val email by viewModel.email.observeAsState("")

    val context = LocalContext.current




    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))

        // Campos
        NameField(name) { viewModel.onNameChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        LastNameField(lastName) { viewModel.onLastNameChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        SecondLastNameField(secondLastName) { viewModel.onSecondLastNameChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        BirthDayField(birthDay) { viewModel.onBirthDayChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        StateField(state) { viewModel.onStateChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        MunicipalityField(municipality) { viewModel.onMunicipalityChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        ColonyField(colony) { viewModel.onColonyChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        StreetField(street) { viewModel.onStreetChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        EmailField(email) { viewModel.onEmailChange(it) }
        Spacer(modifier = Modifier.height(10.dp))

        TelephoneField(telephone) { viewModel.onTelephoneChange(it) }
        Spacer(modifier = Modifier.height(10.dp))



        Button(
            onClick = {
                viewModel.registerUser(
                    name = name,
                    lastName = lastName,
                    secondLastName = secondLastName,
                    email = email,
                    telephone = telephone,
                    birthDay = birthDay,
                    state = state,
                    municipality = municipality,
                    colony = colony,
                    street = street,
                    context = context
                )

                // 3. Guardar que ya se registró (si lo usas para controlar flujo)

            },
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA4937F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFA4937F).copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(text = "Registrar")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Composable
fun RegisterButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA4937F),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFA4937F).copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        Text(text = "Registrar")
    }
}

@Composable
fun HeaderImage(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = modifier.size(300.dp)
        )
    }
}


@Composable
fun BirthDayField(value: String, onValueChange: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val year = remember { mutableStateOf(2024) }
    val month = remember { mutableStateOf(1) }
    val day = remember { mutableStateOf(1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Selecciona tu fecha") },
            text = {
                Column {
                    Text("Ano", modifier = Modifier.padding(bottom = 4.dp))
                    NumberPicker(
                        value = year.value,
                        onValueChange = { year.value = it },
                        range = 1900..2024
                    )

                    Text("Mes", modifier = Modifier.padding(vertical = 4.dp))
                    NumberPicker(
                        value = month.value,
                        onValueChange = { month.value = it },
                        range = 1..12
                    )

                    Text("Dia", modifier = Modifier.padding(top = 4.dp))
                    NumberPicker(
                        value = day.value,
                        onValueChange = { day.value = it },
                        range = 1..31
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onValueChange("${day.value.toString().padStart(2, '0')}/" +
                                "${month.value.toString().padStart(2, '0')}/" +
                                year.value)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    TextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Nacimiento") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.AddCircle, "Selecciona Fecha")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAF7F4),
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (value > range.first) onValueChange(value - 1) },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, "Decrease")
        }

        Text(
            text = value.toString(),
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { if (value < range.last) onValueChange(value + 1) },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(Icons.Default.KeyboardArrowRight, "Increase")
        }
    }
}

@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit) {
    FieldLine(
        text = "Correo electrónico",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun StateField(value: String, onValueChange: (String) -> Unit){
    FieldLine(
        text = "Estado",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}


@Composable
fun MunicipalityField(value: String, onValueChange: (String) -> Unit){
    FieldLine(
        text = "Municipio",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun ColonyField(value: String, onValueChange: (String) -> Unit){
    FieldLine(
        text = "Colonia",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun StreetField(value: String, onValueChange: (String) -> Unit){
    FieldLine(
        text = "Calle",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun NameField(value: String, onValueChange: (String) -> Unit) {
    FieldLine(
        text = "Nombre",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun LastNameField(value: String, onValueChange: (String) -> Unit) {
    FieldLine(
        text = "Apellido Paterno",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun SecondLastNameField(value: String, onValueChange: (String) -> Unit) {
    FieldLine(
        text = "Apellido Materno",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text
    )
}

@Composable
fun TelephoneField(value: String, onValueChange: (String) -> Unit) {
    FieldLine(
        text = "+11-111-111-1111",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Phone
    )
}

@Composable
fun FieldLine(
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = text, color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAF7F4),
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color(0xFFE4EEFF),
            unfocusedIndicatorColor = Color(0xFFA4937F),
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )
}

