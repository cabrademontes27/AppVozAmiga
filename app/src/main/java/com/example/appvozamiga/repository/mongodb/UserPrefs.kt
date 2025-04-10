package com.example.appvozamiga.repository.mongodb


import android.content.Context

private const val PREFS_NAME = "user_prefs"
private const val KEY_IS_REGISTERED = "is_registered"
private const val KEY_EMAIL_FOR_SIGNIN = "email_for_signin" // Ahora usado para verificar estado si se cierra app

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
