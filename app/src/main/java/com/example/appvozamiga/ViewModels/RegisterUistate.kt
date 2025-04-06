package com.example.appvozamiga.ViewModels

// aqui se verifican los estados en los que se encuentran cada proceso, si hay un error u otra cosa
// si se esta haciendo una operacion o si se mando el codigo
data class RegisterUiState(
    val isCodeSent: Boolean = false,
    val isLoading: Boolean = false,
    val verificationId: String? = null,
    val errorMessage: String? = null,
    val isVerified: Boolean = false,
    val isSuccess: Boolean = false,

)