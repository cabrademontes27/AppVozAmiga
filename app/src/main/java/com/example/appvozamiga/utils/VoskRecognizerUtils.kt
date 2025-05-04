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


    fun startRecognition(context: Context, viewModel: MainViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Copiar modelo
            copyAssetFolder(context, "vosk-model", File(context.filesDir, "model"))

            // 2. Cargar modelo
            val modelPath = File(context.filesDir, "model").absolutePath
            try {
                model = Model(modelPath)
                Log.d("Vosk", "✅ Modelo cargado: $modelPath")
            } catch (e: Exception) {
                Log.e("Vosk", "❌ Error al cargar el modelo", e)
                return@launch
            }

            // 3. Arrancar en Main
            CoroutineScope(Dispatchers.Main).launch {
                val recognizer = Recognizer(model, 16000.0f)
                speechService = SpeechService(recognizer, 16000.0f)
                startListeningLoop(viewModel, context)
            }
        }
    }

    private fun startListeningLoop(viewModel: MainViewModel, context: Context) {
        speechService?.startListening(object : RecognitionListener {
            override fun onPartialResult(hypothesis: String?) {
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
                        Log.d("Vosk", "⚠️ PARCIAL activó comando: foto")
                        lastRecognizedCommand = "foto"
                        handleVoiceCommand("foto", viewModel, context)
                    }
                    partial.contains("lugar") && lastRecognizedCommand != "lugar" -> {
                        Log.d("Vosk", "⚠️ PARCIAL activó comando: lugar")
                        lastRecognizedCommand = "lugar"
                        handleVoiceCommand("lugar", viewModel, context)
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

                Log.d("Vosk", "📤 Final result crudo: $hypothesis")
                if (plain.isBlank()) {
                    Log.d("Vosk", "⛔ Resultado final vacío, no se procesa")
                    startListeningLoop(viewModel, context)
                    return
                }


                Log.d("Vosk", "🔍 Comando a procesar: \"$plain\"")
                handleVoiceCommand(plain, viewModel, context)
                startListeningLoop(viewModel, context)
            }


            override fun onError(e: Exception?) {
                Log.e("Vosk", "❌ Error en reconocimiento", e)
                startListeningLoop(viewModel, context)
            }

            override fun onTimeout() {
                startListeningLoop(viewModel, context)
            }

            override fun onResult(hypothesis: String?) { /* opcional */ }
        })
    }

    fun stopRecognition() {
        speechService?.stop()
        speechService = null
    }

    fun String.estimateSpeechDuration(): Long {
        val palabras = this.split("\\s+".toRegex()).size
        return 500L + palabras * 150L
    }

    fun handleVoiceCommand(cmd: String, viewModel: MainViewModel, context: Context) {
        when {
            cmd.contains("foto") -> {
                TextToSpeechUtils.detener()
                viewModel.setLocked(context, false)

                val mensajeCamara = "Abriendo la cámara"
                val duracion = mensajeCamara.estimateSpeechDuration()

                TextToSpeechUtils.hablarConCallback(mensajeCamara, "CAMARA") {
                    viewModel.navegarARutaPorVoz(Routes.LOADING_TO_CAMERA)

                    Handler(Looper.getMainLooper()).postDelayed({
                        TextToSpeechUtils.liberar()
                    }, duracion + 300)
                }

                lastRecognizedCommand = null
            }

            cmd.contains("lugar") -> {
                TextToSpeechUtils.detener()

                viewModel.obtenerTextoUbicacion(context) { ubicacionTexto ->
                    val mensajeUbicacion = "Según mis datos, $ubicacionTexto"
                    val duracion = mensajeUbicacion.estimateSpeechDuration()

                    TextToSpeechUtils.hablarConCallback(mensajeUbicacion, "UBICACION") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            TextToSpeechUtils.liberar()
                            // 🔄 Después de hablar y liberar, ahora sí desbloqueas la pantalla
                            viewModel.setLocked(context, false, hablarDespedida = false)
                        }, duracion + 500)
                    }
                }

                lastRecognizedCommand = null
            }




            else -> {
                TextToSpeechUtils.hablar("No entendí el comando")
                lastRecognizedCommand = null
            }
        }
    }

}








    /** Copia recursivamente assets/<assetDir> dentro de destDir */
    private fun copyAssetFolder(context: Context, assetDir: String, destDir: File) {
        val assetManager = context.assets
        val files = assetManager.list(assetDir) ?: return
        if (!destDir.exists()) destDir.mkdirs()

        for (fileName in files) {
            val assetPath = if (assetDir.isEmpty()) fileName else "$assetDir/$fileName"
            val outFile   = File(destDir, fileName)
            val subList   = assetManager.list(assetPath)

            if (subList != null && subList.isNotEmpty()) {
                // es un directorio
                copyAssetFolder(context, assetPath, outFile)
            } else {
                // es un archivo, copiar stream
                assetManager.open(assetPath).use { input: InputStream ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
        Log.d("Vosk", "Assets/$assetDir copiado → ${destDir.absolutePath}")
    }



    fun String.removeAccents(): String {
        val nfd = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(nfd, "")
    }