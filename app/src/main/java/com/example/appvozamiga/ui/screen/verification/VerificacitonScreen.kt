package com.example.appvozamiga.ui.screen.verification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerificationScreen(
    phoneNumber: String,
    onVerifyClick: (code: String) -> Unit,
    onResendCode: () -> Unit
) {
    var verificationCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Verificación",
            fontSize = 24.sp,
            color = Color(0xFFA4937F),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Instructions
        Text(
            text = "Ingresa el código de 6 dígitos enviado a\n$phoneNumber",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // OTP Input
        OutlinedTextField(
            value = verificationCode,
            onValueChange = {
                if (it.length <= 6) verificationCode = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Código de verificación") },
            placeholder = { Text("123456") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFAF7F4),
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFFA4937F),
                unfocusedIndicatorColor = Color(0xFFA4937F)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Verify Button
        Button(
            onClick = { onVerifyClick(verificationCode) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = verificationCode.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA4937F),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFA4937F).copy(alpha = 0.5f)
            )
        ) {
            Text("Verificar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resend Code
        TextButton(onClick = onResendCode) {
            Text(
                "Reenviar código",
                color = Color(0xFFA4937F)
            )
        }
    }
}