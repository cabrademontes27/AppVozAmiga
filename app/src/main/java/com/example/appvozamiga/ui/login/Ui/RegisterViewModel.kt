package com.example.appvozamiga.ui.login.Ui

import android.provider.Contacts
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




    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(emailRegex.toRegex()) && email.isNotBlank()
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
}