package com.example.appvozamiga.ViewModels

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvozamiga.repository.mongodb.getUserEmail
import com.example.appvozamiga.repository.mongodb.isUserRegistered
import com.example.appvozamiga.repository.mongodb.setUserRegistered
import com.example.appvozamiga.repository.mongodb.MongoUserRepository
import com.example.appvozamiga.repository.mongodb.models.Location
import com.example.appvozamiga.repository.mongodb.models.UserData
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    //  Firebase
    private val _uiState = mutableStateOf(RegisterUiState())
    val uiState: State<RegisterUiState> = _uiState

    //  formulario
    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _lastName = MutableLiveData("")
    val lastName: LiveData<String> = _lastName

    private val _secondLastName = MutableLiveData("")
    val secondLastName: LiveData<String> = _secondLastName

    private val _telephone = MutableLiveData("")
    val telephone: LiveData<String> = _telephone

    private val _registerEnable = MutableLiveData(false)
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _state = MutableLiveData("")
    val state: LiveData<String> = _state

    private val _birthDay = MutableLiveData("")
    val birthDay: LiveData<String> = _birthDay

    private val _municipality = MutableLiveData("")
    val municipality: LiveData<String> = _municipality

    private val _colony = MutableLiveData("")
    val colony: LiveData<String> = _colony

    private val _street = MutableLiveData("")
    val street: LiveData<String> = _street

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email


    // Lógica de validación
    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^\\+?[0-9]{10,15}$"
        return phone.matches(phoneRegex.toRegex()) && phone.isNotBlank()
    }

    fun validateForm() {
        _registerEnable.value =
            isValidPhone(_telephone.value ?: "") &&
                    (_name.value?.isNotBlank() ?: false) &&
                    (_lastName.value?.isNotBlank() ?: false) &&
                    isValidEmail(_email.value ?: "")
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Métodos de cambio de campo
    fun onNameChange(newName: String) {
        _name.value = newName; validateForm()
    }

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName; validateForm()
    }

    fun onSecondLastNameChange(newSecondLastName: String) {
        _secondLastName.value = newSecondLastName; validateForm()
    }

    fun onTelephoneChange(newTelephone: String) {
        _telephone.value = newTelephone; validateForm()
    }

    fun onBirthDayChange(newBirthDay: String) {
        _birthDay.value = newBirthDay; validateForm()
    }

    fun onStateChange(newState: String) {
        _state.value = newState; validateForm()
    }

    fun onMunicipalityChange(newMunicipality: String) {
        _municipality.value = newMunicipality; validateForm()
    }

    fun onColonyChange(newColony: String) {
        _colony.value = newColony; validateForm()
    }

    fun onStreetChange(newStreet: String) {
        _street.value = newStreet; validateForm()
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail; validateForm()
    }

    fun registerUser(
        name: String,
        lastName: String,
        secondLastName: String,
        email: String,
        telephone: String,
        birthDay: String,
        state: String,
        municipality: String,
        colony: String,
        street: String,
        context: Context
    ) {
        _name.value = name
        _lastName.value = lastName
        _secondLastName.value = secondLastName
        _email.value = email
        _telephone.value = telephone
        _birthDay.value = birthDay
        _state.value = state
        _municipality.value = municipality
        _colony.value = colony
        _street.value = street

        // Guardar en SharedPreferences como respaldo
        saveUserDataToPrefs(context)

        val location = Location(state, municipality, colony, street)
        val user = UserData(name, lastName, secondLastName, email, telephone, birthDay, location)

        viewModelScope.launch {
            val success = MongoUserRepository.registerUser(user)
            if (success) {
                _uiState.value = _uiState.value.copy(isSuccess = true)
                Log.i("RegisterViewModel", "Usuario registrado en MongoDB")
                Toast.makeText(
                    context,
                    "Registro exitoso. Revisa tu correo para verificar.",
                    Toast.LENGTH_LONG
                ).show()

                verificarConReintentos(context)
            } else {
                Log.e("RegisterViewModel", "Error al registrar usuario en MongoDB")
            }
        }
    }


    //aqui verificamos que el link ya se haya verificado o sifo abierto
    fun setUserVerified(context: Context) {
        _uiState.value = _uiState.value.copy(isVerified = true)
        Log.i("RegisterViewModel", "Usuario verificado por token en backend")
    }


    fun saveUserDataToPrefs(context: Context) {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", name.value)
            putString("lastName", lastName.value)
            putString("secondLastName", secondLastName.value)
            putString("email", email.value)
            putString("telephone", telephone.value)
            putString("birthDay", birthDay.value)
            putString("state", state.value)
            putString("municipality", municipality.value)
            putString("colony", colony.value)
            putString("street", street.value)
            apply()
        }
    }

    fun verificarToken(tokenId: String, context: Context) {
        viewModelScope.launch {
            try {
                val result = MongoUserRepository.verifyToken(tokenId)
                Log.d("RegisterViewModel", "Resultado backend al verificar token: $result")


                if (result == "valid") {
                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putBoolean("is_registered", true)
                        .putString("user_token", tokenId)
                        .apply()

                    setUserRegistered(context)
                    setUserVerified(context)
                    goToMainMenu()

                    Log.d("RegisterViewModel", "Token verificado correctamente")
                } else {
                    Log.e("RegisterViewModel", "Token inválido o no recibido")
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error verificando token", e)
            }
        }
    }

    fun handleStartupIntent(intent: Intent?, context: Context) {
        val tokenId = intent?.data?.getQueryParameter("id")
        Log.d("RegisterViewModel", "Token recibido desde intent: $tokenId")


        if (tokenId != null) {
            verificarToken(tokenId, context)
        } else if (!isUserRegistered(context)) {
            verificarEstadoDesdeBackend(context)
        }
    }
    //esta parte actulizara los datos que reciba el main y lo mandarra
    // al navigation


    fun goToMainMenu() {
        _uiState.value = _uiState.value.copy(shouldNavigateToMenu = true)
    }
    fun resetNavigationFlag() {
        _uiState.value = _uiState.value.copy(shouldNavigateToMenu = false)
    }


    fun verificarEstadoDesdeBackend(context: Context) {
        val email = getUserEmail(context) ?: return
        Log.d("RegisterViewModel", "Consultando verificación para: $email")

        viewModelScope.launch {
            try {
                val response = MongoUserRepository.checkEmailVerified(email)

                if (response?.verified == true) {
                    setUserRegistered(context)
                    setUserVerified(context)
                    goToMainMenu()
                } else {
                    Log.d("RegisterViewModel", "El correo aún no ha sido verificado")
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Error verificando email con Retrofit", e)
            }
        }
    }

    private fun verificarConReintentos(context: Context) {
        val email = _email.value ?: return

        viewModelScope.launch {
            repeat(6) { intento ->
                val response = MongoUserRepository.checkEmailVerified(email)

                if (response?.verified == true) {
                    setUserRegistered(context)
                    setUserVerified(context)
                    goToMainMenu()
                    Log.d("RegisterViewModel", "Verificación detectada en intento ${intento + 1}")
                    return@launch
                }

                Log.d("RegisterViewModel", "Intento ${intento + 1}: aún no verificado")
                kotlinx.coroutines.delay(10000)
            }

            Log.d("RegisterViewModel", "No se verificó después de varios intentos")
        }
    }

}