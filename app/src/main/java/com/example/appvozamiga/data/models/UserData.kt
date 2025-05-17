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
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val bloodType: String = "",
    val disabilityDescription: String = ""
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

data class SosRequest(
    val to: List<String>,
    val message: String
)

data class UbicacionRequest(
    val email: String,
    val lat: Double,
    val lon: Double
)

data class ControlledMedication(
    val _id: String? = null,
    val name: String,
    val description: String,
    val email: String,
    val startDateTime: Long, // timestamp en millis
    val endDateTime: Long,
    val intervalHours: Int
)


