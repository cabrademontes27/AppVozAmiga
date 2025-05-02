package com.example.appvozamiga.utils

import android.speech.tts.TextToSpeech
import android.content.Context
import android.os.Bundle
import java.util.Locale
import android.speech.tts.UtteranceProgressListener
import android.util.Log

object TextToSpeechUtils {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun iniciarTTS(context: Context, onReady: (() -> Unit)? = null) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale("es", "MX")
                    isInitialized = true
                    onReady?.invoke()
                } else {
                    Log.e("TTS", "Error inicializando TTS: status=$status")
                }
            }
        } else {
            onReady?.invoke()
        }
    }


    fun hablar(texto: String) {
        if (isInitialized) {
            tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTS", "Intento de hablar antes de inicializar")
        }
    }

    fun detener() {
        tts?.stop()
    }

    fun liberar() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    fun hablarConCallback(
        texto: String,
        utteranceId: String,
        onDone: () -> Unit
    ) {
        if (!isInitialized) return

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String) {}
            override fun onError(id: String) {}
            override fun onDone(id: String) {
                if (id == utteranceId) {
                    onDone()
                }
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }
}