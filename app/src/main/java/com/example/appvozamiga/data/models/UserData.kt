package com.example.appvozamiga.data.models

data class UserData(
    val name: String = "",
    val lastName: String = "",
    val secondLastName: String = "",
    val email: String = "",
    val password: String = "",
    val telephone: String = "",
    val birthDay: String = "",
    val location: Location = Location(),
    val emergencyContacts: List<EmergencyContact> = emptyList()
)

data class Location(
    val state: String = "",
    val municipality: String = "",
    val colony: String = "",
    val street: String = ""
)

data class EmergencyContact(
    val name: String = "",
    val relation: String = "",
    val phone: String = ""
)