package com.example.appvozamiga.ViewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvozamiga.repository.fireBase.AuthRepositorySms
import com.example.appvozamiga.repository.fireBase.AuthRepositoryEmail
import com.example.appvozamiga.repository.mongodb.MongoUserRepository
import com.example.appvozamiga.repository.mongodb.models.Location
import com.example.appvozamiga.repository.mongodb.models.UserData
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    //  Firebase
    private val firebaseAuthRepo = AuthRepositorySms()
    private val _uiState = mutableStateOf(RegisterUiState())
    val uiState: State<RegisterUiState> = _uiState
    private val auth = FirebaseAuth.getInstance()
    private val firebaseAuthEmailRepo = AuthRepositoryEmail()

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

    private  val _email = MutableLiveData("")
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
    fun onNameChange(newName: String) { _name.value = newName; validateForm() }
    fun onLastNameChange(newLastName: String) { _lastName.value = newLastName; validateForm() }
    fun onSecondLastNameChange(newSecondLastName: String) { _secondLastName.value = newSecondLastName; validateForm() }
    fun onTelephoneChange(newTelephone: String) { _telephone.value = newTelephone; validateForm() }
    fun onBirthDayChange(newBirthDay: String) { _birthDay.value = newBirthDay; validateForm() }
    fun onStateChange(newState: String) { _state.value = newState; validateForm() }
    fun onMunicipalityChange(newMunicipality: String) { _municipality.value = newMunicipality; validateForm() }
    fun onColonyChange(newColony: String) { _colony.value = newColony; validateForm() }
    fun onStreetChange(newStreet: String) { _street.value = newStreet; validateForm() }
    fun onEmailChange(newEmail: String){_email.value = newEmail; validateForm()}


    // aqui lo que se hacia era validar el codigo que se mandaba por sms
    fun verifyCodeManually(code: String) {
        val id = _uiState.value.verificationId ?: return
        val credential = firebaseAuthRepo.getCredential(id, code)
        signInWithCredential(credential)
    }
    //esta funcion es igual utilizada para sms entonces por ahora no tiene utilidad
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        firebaseAuthRepo.signInWithPhoneAuthCredential(
            credential,
            onSuccess = {
                _uiState.value = _uiState.value.copy(isLoading = false, isVerified = true)
            },
            onFailure = { exception ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.localizedMessage)
            }
        )
    }

    fun registerUser() {
        // Aquí va el guardado del usuario
    }


    fun sendMagicLink(email: String, context: Context) {
        if (!isValidEmail(email)) {
            Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuthEmailRepo.sendMagicLink(email, context) { success, error ->
            if (success) {
                Toast.makeText(context, "Correo enviado correctamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
        }
    }


    //aqui verificamos que el link ya se haya verificado o sifo abierto
    fun setUserVerified(){
        _uiState.value = _uiState.value.copy(isVerified = true)

        // Esta parte sera para las importacion a la base de datos en mongodb
        val location = Location(
            state = state.value ?: "",
            municipality = municipality.value ?: "",
            colony = colony.value ?: "",
            street = street.value ?: ""
        )

        val user = UserData(
            name = name.value ?: "",
            lastName = lastName.value ?: "",
            secondLastName = secondLastName.value ?: "",
            email = email.value ?: "",
            telephone = telephone.value ?: "",
            birthDay = birthDay.value ?: "",
            location = location
        )


        viewModelScope.launch {
            val success = MongoUserRepository.registerUser(user)
            if (success) {
                _uiState.value = _uiState.value.copy(isSuccess = true)
            } else {
                Log.e("ViewModel", "Error al registrar el usuario en MongoDB")
            }
        }
    }






}