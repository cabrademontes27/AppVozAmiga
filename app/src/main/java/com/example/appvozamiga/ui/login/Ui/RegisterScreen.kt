package com.example.appvozamiga.ui.login.Ui

import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appvozamiga.R

@Composable
fun RegisterScreen(viewModel: RegisterViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Register(
            modifier = Modifier.align(Alignment.Center),
            viewModel = viewModel
        )
    }
}

@Composable
fun Register(modifier: Modifier, viewModel: RegisterViewModel) {

    val email by viewModel.email.observeAsState("")
    val name by viewModel.name.observeAsState("")
    val lastName by viewModel.lastName.observeAsState("")
    val secondLastName by viewModel.secondLastName.observeAsState("")
    val telephone by viewModel.telephone.observeAsState("")
    val isEnabled by viewModel.registerEnable.observeAsState(false)

    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))

        NameField(name) { viewModel.onNameChange(it) }
        Spacer(modifier = Modifier.padding(10.dp))

        LastNameField(lastName) { viewModel.onLastNameChange(it) }
        Spacer(modifier = Modifier.padding(10.dp))

        SecondLastNameField(secondLastName) { viewModel.onSecondLastNameChange(it) }
        Spacer(modifier = Modifier.padding(10.dp))

        TelephoneField(telephone) { viewModel.onTelephoneChange(it) }
        Spacer(modifier = Modifier.padding(10.dp))

        EmailField(email) { viewModel.onEmailChange(it) }
        Spacer(modifier = Modifier.padding(10.dp))
        NextButton(enabled = isEnabled,
            onClick = { viewModel.registerUser() } )
    }
}


@Composable
fun NextButton(enabled: Boolean, onClick: () -> Unit) {
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
        Text(text = "Siguiente")
    }
}

@Composable
fun EmailField(email: String, onTextFieldChange:(String) -> Unit) {
    TextField(
        value = email,
        onValueChange = {onTextFieldChange(it)},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
        text = "Teléfono",
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Phone
    )
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
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = modifier
                .size(300.dp)
        )
    }
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

