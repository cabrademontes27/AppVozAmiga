package com.example.appvozamiga.ui.login.Ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import  androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appvozamiga.R

@Composable
fun RegisterScreen(){
    Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){
        Register(Modifier.align(Alignment.Center))
    }
}

@Composable
fun Register(modifier: Modifier){
    Column (modifier = modifier){
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        NameField()
        Spacer(modifier = Modifier.padding(10.dp))
        LastNameField()
        Spacer(modifier = Modifier.padding(10.dp))
        SecondLastNameField()
        Spacer(modifier = Modifier.padding(10.dp))
        TelephoneField()
        Spacer(modifier = Modifier.padding(10.dp))
        EmailFIeld()
        Spacer(modifier = Modifier.padding(10.dp))
        NextButton()


    }
}

@Composable
fun NextButton(){
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA4937F),
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Siguiente")
    }
}

@Composable
fun EmailFIeld() {
    FieldLine("Email")
}

@Composable
fun TelephoneField() {
    FieldLine("Telefono")
}

@Composable
fun SecondLastNameField() {
    FieldLine("Apellido Materno")
}

@Composable
fun LastNameField() {
    FieldLine("Apellido Paterno")
}




@Composable
fun NameField() {
    FieldLine("Nombre")
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
fun FieldLine(text : String){
    TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = text, color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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