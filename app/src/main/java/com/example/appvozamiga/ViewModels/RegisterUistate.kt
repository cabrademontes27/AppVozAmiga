package com.example.appvozamiga.ui.Screen.register

data class RegisterUiState(
    val isCodeSent: Boolean = false,
    val isLoading: Boolean = false,
    val verificationId: String? = null,
    val errorMessage: String? = null,
    val isVerified: Boolean = false
)