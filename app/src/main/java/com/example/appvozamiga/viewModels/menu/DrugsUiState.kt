package com.example.appvozamiga.viewModels.menu

import com.example.appvozamiga.data.models.Medications


data class DrugsUiState(
    val medications: List<Medications> = emptyList(),
    val showAddDialog: Boolean = false,
    val nombreNuevo: String = "",
    val descripcionNueva: String = "",
    val selectedDrug: Medications? = null,
    val descripcionEditada: String = ""
)