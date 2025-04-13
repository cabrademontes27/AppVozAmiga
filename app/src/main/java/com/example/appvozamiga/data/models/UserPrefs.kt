package com.example.appvozamiga.data.models


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "user_prefs"
private const val KEY_IS_REGISTERED = "is_registered"
private const val KEY_EMAIL_FOR_SIGNIN = "email_for_signin" // Ahora usado para verificar estado si se cierra app
private const val KEY_MEDICAMENTOS = "medicamentos_guardados"

fun setUserRegistered(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_IS_REGISTERED, true).apply()
}

fun isUserRegistered(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_IS_REGISTERED, false)
}

fun saveSignInEmail(context: Context, email: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_EMAIL_FOR_SIGNIN, email).apply()
}

fun getUserEmail(context: Context): String? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString(KEY_EMAIL_FOR_SIGNIN, null)
}

fun clearUserPrefs(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}

fun saveUserProfile(context: Context, user: UserData) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().apply {
        putString("name", user.name)
        putString("lastName", user.lastName)
        putString("secondLastName", user.secondLastName)
        putString("email", user.email)
        putString("telephone", user.telephone)
        putString("birthDay", user.birthDay)
        putString("state", user.location.state)
        putString("municipality", user.location.municipality)
        putString("colony", user.location.colony)
        putString("street", user.location.street)
        apply()
    }
}

fun loadUserProfile(context: Context): UserData? {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val name = prefs.getString("name", null) ?: return null
    val lastName = prefs.getString("lastName", null) ?: return null
    val secondLastName = prefs.getString("secondLastName", null) ?: return null
    val email = prefs.getString("email", null) ?: return null
    val telephone = prefs.getString("telephone", null) ?: return null
    val birthDay = prefs.getString("birthDay", null) ?: return null

    val state = prefs.getString("state", "") ?: ""
    val municipality = prefs.getString("municipality", "") ?: ""
    val colony = prefs.getString("colony", "") ?: ""
    val street = prefs.getString("street", "") ?: ""

    val location = Location(state, municipality, colony, street)

    return UserData(name, lastName, secondLastName, email, telephone, birthDay, location)
}

// aqui guardaremos las preferencias o de forma local pero para medicamentos
// despues moverlo hacia otra carpeta


fun saveMedicamentos(context: Context, lista: List<Medicamento>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = Gson().toJson(lista)
    prefs.edit().putString(KEY_MEDICAMENTOS, json).apply()
}

fun loadMedicamentos(context: Context): List<Medicamento> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(KEY_MEDICAMENTOS, null)
    return if (json != null) {
        val type = object : TypeToken<List<Medicamento>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}

