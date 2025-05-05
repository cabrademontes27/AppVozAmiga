package com.example.appvozamiga.ui.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.viewModels.login.LoginViewModel
import com.example.appvozamiga.viewModels.login.LoginUiState
import com.example.appvozamiga.ui.navigation.Routes
import android.annotation.SuppressLint
import androidx.compose.ui.platform.LocalContext
import com.example.appvozamiga.data.models.saveSignInEmail
import com.example.appvozamiga.data.models.setUserRegistered


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value       = email,
            onValueChange = { email = it },
            label       = { Text("Correo electrónico") },
            singleLine  = true,
            modifier    = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value                = password,
            onValueChange        = { password = it },
            label                = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine           = true,
            modifier             = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick  = { viewModel.login(email.trim(), password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar")
            }
        }
        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                viewModel.resetState()
                navController.navigate(Routes.REGISTER) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
        Spacer(Modifier.height(12.dp))

        when (uiState) {
            is LoginUiState.Error -> Text(
                (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            is LoginUiState.Success -> {
                // Guardar preferencias y navegar solo una vez
                LaunchedEffect(uiState) {
                    setUserRegistered(context)
                    saveSignInEmail(context, (uiState as LoginUiState.Success).user.email)
                    onLoginSuccess()
                    viewModel.resetState()
                }
            }
            else -> {}
        }
    }
}
