package com.example.appvozamiga.ui.login.Ui

import android.provider.Contacts
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

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

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private  val _state = MutableLiveData("")
    val state : LiveData<String> = _state

    private  val _birthDay = MutableLiveData("")
    val birthDay : LiveData<String> = _birthDay

    private  val _municipality = MutableLiveData("")
    val municipality : LiveData<String> = _municipality

    private  val _colony = MutableLiveData("")
    val  colony : LiveData<String> = _colony

    private  val _street = MutableLiveData("")
    val street : LiveData<String> = _street




    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotBlank()
    }

    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^\\+?[0-9]{10,15}\$"
        return phone.matches(phoneRegex.toRegex()) && phone.isNotBlank()
    }

    fun validateForm() {
        _emailError.value = if (isValidEmail(_email.value ?: "")) null else "Invalid email"
        _registerEnable.value = isValidEmail(_email.value ?: "") &&
                isValidPhone(_telephone.value ?: "") &&
                (_name.value?.isNotBlank() ?: false) &&
                (_lastName.value?.isNotBlank() ?: false)
        //agregar las demas validaciones para state, birday ect
    }

    fun registerUser(){
        // deberia ir donde se agrega el usuario
    }
    // Field change handlers with auto-validation
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        validateForm()
    }

    fun onNameChange(newName: String) {
        _name.value = newName
        validateForm()
    }

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName
        validateForm()
    }

    fun onSecondLastNameChange(newSecondLastName: String) {
        _secondLastName.value = newSecondLastName
        validateForm()
    }

    fun onTelephoneChange(newTelephone: String) {
        _telephone.value = newTelephone
        validateForm()
    }

    fun onBirthDayChange(newBirthDay: String) {
        _birthDay.value = newBirthDay
        validateForm()
    }

    fun onStateChange(newState: String) {
        _state.value = newState
        validateForm()
    }

    fun onMunicipalityChange(newMunicipality: String){
        _municipality.value = newMunicipality
        validateForm()
    }

    fun onColonyChange(newColony: String){
        _colony.value = newColony
        validateForm()
    }

    fun onStreetChange(newStreet: String){
        _street.value = newStreet
        validateForm()
    }





}