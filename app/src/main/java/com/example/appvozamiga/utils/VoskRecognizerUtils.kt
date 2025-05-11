package com.example.appvozamiga.utils


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.appvozamiga.ui.navigation.Routes
import com.example.appvozamiga.viewModels.menu.MainViewModel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import org.json.JSONObject
import java.text.Normalizer


object VoskRecognizerUtils {

    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var lastRecognizedCommand: String? = null
    private var isSpeaking = false

    fun startRecognition(context: Context, viewModel: MainViewModel) {
        Log.d("Vosk", "🟢 Iniciando reconocimiento de voz...")
        CoroutineScope(Dispatchers.IO).launch {
            copyAssetFolder(context, "vosk-model", File(context.filesDir, "model"))
            val modelPath = File(context.filesDir, "model").absolutePath
            try {
                model = Model(modelPath)
                Log.d("Vosk", "✅ Modelo cargado: $modelPath")
            } catch (e: Exception) {
                Log.e("Vosk", "❌ Error al cargar el modelo", e)
                return@launch
            }

            CoroutineScope(Dispatchers.Main).launch {
                val recognizer = Recognizer(model, 16000.0f)
                speechService = SpeechService(recognizer, 16000.0f)
                startListeningLoop(viewModel, context)
            }
        }
    }

    private fun startListeningLoop(viewModel: MainViewModel, context: Context) {
        Log.d("Vosk", "👂 Empezando a escuchar...")
        speechService?.startListening(object : RecognitionListener {
            override fun onPartialResult(hypothesis: String?) {
                if (isSpeaking) return
                val partial = hypothesis
                    ?.let { JSONObject(it).optString("partial") }
                    .orEmpty()
                    .removeAccents()
                    .lowercase()
                    .trim()

                viewModel.actualizarTextoReconocido(partial)
                Log.d("Vosk", "🟡 Parcial recibido: $partial")

                when {
                    partial.contains("foto") && lastRecognizedCommand != "foto" -> {
                        lastRecognizedCommand = "foto"
                        Log.d("Vosk", "⚠️ Reconocido comando: foto")
                        handleVoiceCommand("foto", viewModel, context)
                    }
                    partial.contains("lugar") && lastRecognizedCommand != "lugar" -> {
                        lastRecognizedCommand = "lugar"
                        Log.d("Vosk", "⚠️ Reconocido comando: lugar")
                        handleVoiceCommand("lugar", viewModel, context)
                    }
                    partial.contains("medicamentos") && lastRecognizedCommand != "medicamentos" -> {
                        lastRecognizedCommand = "medicamentos"
                        Log.d("Vosk", "⚠️ Reconocido comando: medicamentos")
                        handleVoiceCommand("medicamentos", viewModel, context)
                    }
                    partial.contains("codigo") && lastRecognizedCommand != "qr" -> {
                        lastRecognizedCommand = "qr"
                        Log.d("Vosk", "⚠️ Reconocido comando: codigo/qr")
                        handleVoiceCommand("qr", viewModel, context)
                    }
                }
            }

            override fun onFinalResult(hypothesis: String?) {
                val plain = hypothesis
                    ?.let { JSONObject(it).optString("text").ifBlank { JSONObject(it).optString("partial") } }
                    ?.removeAccents()
                    ?.lowercase()
                    ?.trim()
                    ?: ""

                Log.d("Vosk", "📤 Resultado final: \"$plain\"")

                // ✅ Ignorar si ya se ejecutó el comando parcial
                if (plain.isBlank() || (lastRecognizedCommand != null && plain.contains(lastRecognizedCommand!!))) {
                    Log.d("Vosk", "⛔ Resultado final ignorado (ya procesado o vacío)")
                    return
                }

                handleVoiceCommand(plain, viewModel, context)
            }


            override fun onError(e: Exception?) {
                Log.e("Vosk", "❌ Error en reconocimiento", e)
            }

            override fun onTimeout() {
                Log.w("Vosk", "⏰ Reconocimiento terminado por inactividad")
            }

            override fun onResult(hypothesis: String?) {}
        })
    }

    fun stopRecognition() {
        Log.d("Vosk", "🛑 Reconocimiento detenido")
        speechService?.stop()
        speechService = null
    }

    fun String.estimateSpeechDuration(): Long {
        val palabras = this.split("\\s+".toRegex()).size
        return 500L + palabras * 150L
    }

    fun handleVoiceCommand(cmd: String, viewModel: MainViewModel, context: Context) {
        stopRecognition()
        Log.d("Vosk", "▶️ Ejecutando comando: $cmd")

        when {
            cmd.contains("foto") -> {
                TextToSpeechUtils.detener()
                viewModel.setLocked(context, false)

                val mensaje = "Abriendo la cámara"
                val duracion = mensaje.estimateSpeechDuration()

                TextToSpeechUtils.hablarConCallback(mensaje, "CAMARA") {
                    Log.d("TTS", "✅ TTS finalizado para: CAMARA")
                    viewModel.navegarARutaPorVoz(Routes.CAMERA)
                    Handler(Looper.getMainLooper()).postDelayed({
                        TextToSpeechUtils.liberar()
                        lastRecognizedCommand = null
                    }, duracion + 300)
                }
            }

            cmd.contains("lugar") -> {
                TextToSpeechUtils.detener()
                viewModel.obtenerTextoUbicacion(context) { ubicacionTexto ->
                    val mensaje = "Según mis datos, $ubicacionTexto"
                    val duracion = mensaje.estimateSpeechDuration()

                    TextToSpeechUtils.hablarConCallback(mensaje, "UBICACION") {
                        Log.d("TTS", "✅ TTS finalizado para: UBICACION")
                        Handler(Looper.getMainLooper()).postDelayed({
                            TextToSpeechUtils.liberar()
                            viewModel.setLocked(context, false, hablarDespedida = false)
                            lastRecognizedCommand = null
                        }, duracion + 500)
                    }
                }
            }

            cmd.contains("codigo") || cmd.contains("qr") -> {
                TextToSpeechUtils.detener()
                val mensaje = "Mostrando código"
                val duracion = mensaje.estimateSpeechDuration()

                TextToSpeechUtils.hablarConCallback(mensaje, "QR") {
                    Log.d("TTS", "✅ TTS finalizado para: QR")
                    viewModel.navegarARutaPorVoz(Routes.QR)
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.setLocked(context, false, hablarDespedida = true)
                        lastRecognizedCommand = null
                    }, duracion + 300)
                }
            }

            cmd.contains("medicamentos") -> {
                TextToSpeechUtils.detener()
                isSpeaking = true

                val lista = viewModel.medicationsUiState.medications
                val total = lista.size
                val nombres =
                    lista.map { it.name.trim() }.filter { it.isNotBlank() }.joinToString(", ")

                val mensaje = when {
                    total == 0 -> "No tienes medicamentos registrados."
                    total == 1 -> "Tienes un medicamento registrado. Su nombre es: $nombres"
                    else -> "Tienes un total de $total medicamentos. Sus nombres son: $nombres"
                }

                val duracion = mensaje.estimateSpeechDuration()

                TextToSpeechUtils.hablarConCallback(mensaje, "MED_LIST") {
                    Log.d("TTS", "✅ TTS finalizado para: MED_LIST")
                    isSpeaking = false

                    Handler(Looper.getMainLooper()).postDelayed({
                        TextToSpeechUtils.hablarConCallback("Hasta luego", "BYE") {
                            Log.d("TTS", "👋 Despedida finalizada")
                            TextToSpeechUtils.liberar()
                            viewModel.setLocked(
                                context,
                                false,
                                hablarDespedida = false
                            ) // ✅ evitar segunda despedida
                            lastRecognizedCommand = null
                        }
                    }, 1000)
                }
            }

                else -> {
                Log.d("Vosk", "❓ Comando no reconocido")
                TextToSpeechUtils.hablar("No entendí el comando")
                lastRecognizedCommand = null
            }
        }
    }

    private fun copyAssetFolder(context: Context, assetDir: String, destDir: File) {
        val assetManager = context.assets
        val files = assetManager.list(assetDir) ?: return
        if (!destDir.exists()) destDir.mkdirs()

        for (fileName in files) {
            val assetPath = if (assetDir.isEmpty()) fileName else "$assetDir/$fileName"
            val outFile = File(destDir, fileName)
            val subList = assetManager.list(assetPath)

            if (subList != null && subList.isNotEmpty()) {
                copyAssetFolder(context, assetPath, outFile)
            } else {
                assetManager.open(assetPath).use { input: InputStream ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
        Log.d("Vosk", "📦 Modelo copiado desde assets/$assetDir a ${destDir.absolutePath}")
    }

    fun String.removeAccents(): String {
        val nfd = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(nfd, "")
    }
}



// ESTA FUNCION SE UTILIZARA CUANDO TODOS LOS COMANDOS ESTEN FINALIZADOS ASI VOLVEREMOS A HACERLO UN ASISTENTE
//*
// private fun startListeningLoop(viewModel: MainViewModel, context: Context) {
//        speechService?.startListening(object : RecognitionListener {
//            override fun onPartialResult(hypothesis: String?) {
//                val partial = hypothesis
//                    ?.let { JSONObject(it).optString("partial") }
//                    .orEmpty()
//                    .removeAccents()
//                    .lowercase()
//                    .trim()
//
//                viewModel.actualizarTextoReconocido(partial)
//                Log.d("Vosk", "🟡 Parcial recibido: $partial")
//
//                when {
//                    partial.contains("foto") && lastRecognizedCommand != "foto" -> {
//                        Log.d("Vosk", "⚠️ PARCIAL activó comando: foto")
//                        lastRecognizedCommand = "foto"
//                        handleVoiceCommand("foto", viewModel, context)
//                    }
//                    partial.contains("lugar") && lastRecognizedCommand != "lugar" -> {
//                        Log.d("Vosk", "⚠️ PARCIAL activó comando: lugar")
//                        lastRecognizedCommand = "lugar"
//                        handleVoiceCommand("lugar", viewModel, context)
//                    }
//                    partial.contains("codigo") && lastRecognizedCommand != "codigo" -> {
//                        Log.d("Vosk", "⚠️ PARCIAL activó comando: codigo")
//                        lastRecognizedCommand = "codigo"
//                        handleVoiceCommand("codigo", viewModel, context)
//                    }
//                    partial.contains("qr") && lastRecognizedCommand != "qr" -> {
//                        Log.d("Vosk", "⚠️ PARCIAL activó comando: qr")
//                        lastRecognizedCommand = "qr"
//                        handleVoiceCommand("qr", viewModel, context)
//                    }
//                }
//            }
//
//
//
//            override fun onFinalResult(hypothesis: String?) {
//                val plain = hypothesis
//                    ?.let { JSONObject(it).optString("text").ifBlank { JSONObject(it).optString("partial") } }
//                    ?.removeAccents()
//                    ?.lowercase()
//                    ?.trim()
//                    ?: ""
//
//                Log.d("Vosk", "📤 Final result crudo: $hypothesis")
//                if (plain.isBlank()) {
//                    Log.d("Vosk", "⛔ Resultado final vacío, no se procesa")
//                    startListeningLoop(viewModel, context)
//                    return
//                }
//
//
//                Log.d("Vosk", "🔍 Comando a procesar: \"$plain\"")
//                handleVoiceCommand(plain, viewModel, context)
//                startListeningLoop(viewModel, context)
//            }
//
//
//            override fun onError(e: Exception?) {
//                Log.e("Vosk", "❌ Error en reconocimiento", e)
//                startListeningLoop(viewModel, context)
//            }
//
//            override fun onTimeout() {
//                startListeningLoop(viewModel, context)
//            }
//
//            override fun onResult(hypothesis: String?) { /* opcional */ }
//        })
//    }*//