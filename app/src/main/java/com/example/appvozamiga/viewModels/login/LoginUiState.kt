package com.example.appvozamiga.viewModels.login

import com.example.appvozamiga.data.models.UserData

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: UserData) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}