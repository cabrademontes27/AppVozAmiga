package com.example.appvozamiga.data.models

data class UserData(
    val name: String = "",
    val lastName: String = "",
    val secondLastName: String = "",
    val email: String = "",
    val password: String = "",
    val telephone: String = "",
    val birthDay: String = "",
    val location: Location = Location()
)

data class Location(
    val state: String = "",
    val municipality: String = "",
    val colony: String = "",
    val street: String = ""
)