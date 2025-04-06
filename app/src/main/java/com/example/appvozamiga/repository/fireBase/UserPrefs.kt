package com.example.appvozamiga.backend.fireBase


import android.content.Context

private const val PREFS_NAME = "app_prefs"
private const val KEY_IS_REGISTERED = "is_registered"

fun setUserRegistered(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_IS_REGISTERED, true).apply()
}

fun isUserRegistered(context: Context): Boolean {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("isUserRegistered", false)
}
