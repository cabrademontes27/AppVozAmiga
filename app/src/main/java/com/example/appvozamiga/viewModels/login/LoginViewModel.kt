package com.example.appvozamiga.viewModels.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvozamiga.data.repository.MongoUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        // 1) Validar campos
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email y contraseña son requeridos")
            return
        }

        // 2) Mostrar carga
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                // 3) Verificar email confirmado
                val status = MongoUserRepository.checkEmailVerified(email)
                if (status == null) {
                    _uiState.value = LoginUiState.Error("No se pudo verificar el email")
                    return@launch
                }
                if (!status.verified) {
                    _uiState.value = LoginUiState.Error("La cuenta no está verificada")
                    return@launch
                }

                // 4) Obtener datos de usuario
                val user = MongoUserRepository.getUserByEmail(email)
                if (user == null) {
                    _uiState.value = LoginUiState.Error("Usuario no encontrado")
                    return@launch
                }

                // 5) Aquí, cuando tengas el endpoint, validas password:
                //    val loginOk = MongoUserRepository.login(email, password)
                //    if (!loginOk) { …error… }

                // 6) Éxito
                _uiState.value = LoginUiState.Success(user)

            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
