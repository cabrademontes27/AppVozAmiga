package com.example.appvozamiga.repository.fireBase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class FirebaseAuthRepositorySms {

    private val auth = FirebaseAuth.getInstance()

    /**
     * Envia un código de verificación al número proporcionado.
     */
    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        Log.d("FirebaseAuthRepo", "📲 Enviando código a: $phoneNumber")

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Genera un credential a partir del ID de verificación y el código ingresado.
     */
    fun getCredential(verificationId: String, code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId, code)
    }

    /**
     * Inicia sesión con un credential (este paso es necesario para que Firebase considere el teléfono como verificado).
     */
    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuthRepo", " signInWithCredential: success")
                    onSuccess()
                } else {
                    Log.w("FirebaseAuthRepo", " signInWithCredential: failure", task.exception)
                    onFailure(task.exception ?: Exception("Unknown error"))
                }
            }
    }
}