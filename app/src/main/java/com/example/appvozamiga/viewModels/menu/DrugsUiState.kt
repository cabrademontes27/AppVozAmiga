package com.example.appvozamiga.viewModels.menu

import com.example.appvozamiga.data.models.Medicamento


data class DrugsUiState(
    val medicamentos: List<Medicamento> = emptyList(),
    val showAddDialog: Boolean = false,
    val nombreNuevo: String = "",
    val descripcionNueva: String = "",
    val selectedDrug: Medicamento? = null,
    val descripcionEditada: String = ""
)