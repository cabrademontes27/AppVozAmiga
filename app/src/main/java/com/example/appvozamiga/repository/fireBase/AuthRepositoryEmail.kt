package com.example.appvozamiga.repository.fireBase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepositoryEmail {

    private val auth = FirebaseAuth.getInstance()

    fun sendMagicLink(email: String, context: Context, onResult: (Boolean, String?) -> Unit) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://vozamiga-001.firebaseapp.com")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.appvozamiga", true, "35")
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthRepoEmail", "Email enviado a $email")

                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("email_for_signin", email).apply()

                    onResult(true, null)
                } else {
                    Log.e("AuthRepoEmail", "Error al enviar el enlace", task.exception)
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }
}