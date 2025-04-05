package com.example.appvozamiga.ui.Screen.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import com.example.appvozamiga.data.firebase.FirebaseAuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import androidx.compose.runtime.State



class RegisterViewModel : ViewModel() {

    //  Firebase
    private val firebaseAuthRepo = FirebaseAuthRepository()
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

    // Lógica de validación
    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^\\+?[0-9]{10,15}$"
        return phone.matches(phoneRegex.toRegex()) && phone.isNotBlank()
    }

    fun validateForm() {
        _registerEnable.value = isValidPhone(_telephone.value ?: "") &&
                (_name.value?.isNotBlank() ?: false) &&
                (_lastName.value?.isNotBlank() ?: false)
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

    // Firebase - envio de codigo
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        firebaseAuthRepo.sendVerificationCode(
            phoneNumber, activity,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isCodeSent = true, verificationId = verificationId)
                }
            }
        )
    }

    fun verifyCodeManually(code: String) {
        val id = _uiState.value.verificationId ?: return
        val credential = firebaseAuthRepo.getCredential(id, code)
        signInWithCredential(credential)
    }

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
}
