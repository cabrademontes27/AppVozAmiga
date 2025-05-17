package com.example.appvozamiga.viewModels.menu

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvozamiga.data.models.UserData
import com.example.appvozamiga.data.models.clearUserPrefs
import com.example.appvozamiga.data.models.getUserEmail
import com.example.appvozamiga.data.models.loadUserProfile
import com.example.appvozamiga.data.models.saveUserProfile
import com.example.appvozamiga.data.network.RetrofitClient
import com.example.appvozamiga.data.repository.MongoUserRepository
import com.example.appvozamiga.utils.TextRecognitionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.runtime.mutableStateOf
import com.example.appvozamiga.data.models.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.appvozamiga.data.models.Medications
import com.example.appvozamiga.data.models.loadMedications
import com.example.appvozamiga.data.models.saveMedications
import android.media.MediaPlayer
import androidx.core.content.ContextCompat
import com.example.appvozamiga.R
import com.example.appvozamiga.utils.TextToSpeechUtils
import android.Manifest
import com.example.appvozamiga.data.models.EmergencyContact
import com.example.appvozamiga.data.models.Location
import com.example.appvozamiga.data.models.getUserId
import com.example.appvozamiga.data.models.loadEmergencyContacts
import com.example.appvozamiga.data.models.saveEmergencyContacts
import com.example.appvozamiga.data.models.saveUserId
import com.example.appvozamiga.utils.VoskRecognizerUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


class MainViewModel(application: Application) : AndroidViewModel(application) {
    val appContext = application.applicationContext

    var recognizedText by mutableStateOf("")
        private set

    var recognizedTextVoice by mutableStateOf("")
        private set

    var locationText by mutableStateOf("Obteniendo ubicación...")
        private set

    var medicationsUiState by mutableStateOf(DrugsUiState())
        private set

    var rutaComando by mutableStateOf<String?>(null)
        private set

    var userId by mutableStateOf<String?>(null)
        private set

    init {
        // Intenta cargar el userId guardado localmente si ya existe
        userId = getUserId(appContext)
    }

    var emergencyContacts by mutableStateOf<List<EmergencyContact>>(emptyList())
        private set

    private var intentosSos = 0
    private var tiempoPrimerSos: Long = 0L

    var currentToken by mutableStateOf(generateToken())
    private var tokenJob: Job? = null








    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    var name = mutableStateOf("")
    var lastName = mutableStateOf("")
    var secondLastName = mutableStateOf("")
    var email = mutableStateOf("")
    var telephone = mutableStateOf("")
    var birthDay = mutableStateOf("")
    var state = mutableStateOf("")
    var municipality = mutableStateOf("")
    var colony = mutableStateOf("")
    var street = mutableStateOf("")
    var bloodType = mutableStateOf("")
    var disabilityDescription = mutableStateOf("")

    //estados para bloquear o desbloquear pantalla
    var appUiState by mutableStateOf(AppUiState())
        private set



    //aqui es solo la parte visual de funcion de la camara,
    //procesar, limpiar y filtrar texto

    fun processImage(
        bitmap: Bitmap,
        onResult: (String) -> Unit
    ) {
        TextRecognitionUtils.recognizeTextFromBitmap(
            bitmap,
            onSuccess = { raw ->
                val textoLimpio = cleanText(raw)
                recognizedText = textoLimpio
                onResult(textoLimpio)
            },
            onFailure = {
                recognizedText = "Error: ${it.localizedMessage}"
                onResult("")
            }
        )
    }

    private fun cleanText(text: String): String {
        return text
            .lines()
            .flatMap { it.split(" ") }
            .map { it.trim() }
            .filter {
                it.length > 2 &&
                        it.all { c -> c.isLetter() || c == '-' } &&
                        it.any { c -> c.isLetter() }
            }
            .joinToString(" ")
    }

    // esta seccion sera para mantener lo escaneado y verificar si hay internet
    // mandarlo a la base de datos, si no, mantenerla ahi localmente



    // aqui va la funcion de ubicacion exacta de dodne me encuentro
    @SuppressLint("MissingPermission")
    fun getLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val loc = result.lastLocation
                    val texto = if (loc != null) {
                        covertLocationToText(loc.latitude, loc.longitude)
                    } else {
                        "No se pudo obtener la ubicación."
                    }
                    // Como getLocation puede venir de un botón, inicializamos TTS aquí:
                    TextToSpeechUtils.iniciarTTS(appContext) {
                        TextToSpeechUtils.hablar(texto)
                    }
                    locationText = texto
                }
            },
            Looper.getMainLooper()
        )
    }

    // 🗺️ Convertir coordenadas a texto entendible
    private fun covertLocationToText(lat: Double, lon: Double): String {
        val geocoder = Geocoder(getApplication(), Locale.getDefault())
        return try {
            val lista = geocoder.getFromLocation(lat, lon, 1)
            if (!lista.isNullOrEmpty()) {
                val dir = lista[0]
                val state  = dir.adminArea ?: ""
                val city   = dir.locality ?: dir.subAdminArea ?: ""
                val colony = dir.subLocality ?: ""
                val street = dir.thoroughfare ?: ""
                "Estás en $state, $city en la colonia $colony en la calle $street"
            } else {
                "No se encontró la dirección."
            }
        } catch (e: Exception) {
            "Error al obtener la dirección: ${e.localizedMessage}"
        }
    }

    fun obtenerUbicacionConTexto(onUbicacionObtenida: (String) -> Unit) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(appContext, permiso) != PackageManager.PERMISSION_GRANTED) {
            onUbicacionObtenida("Permiso de ubicación no concedido.")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val loc = result.lastLocation
                    val texto = if (loc != null) {
                        covertLocationToText(loc.latitude, loc.longitude)
                    } else {
                        "No se pudo obtener la ubicación."
                    }
                    locationText = texto
                    onUbicacionObtenida(texto) // 🔥 Aquí se responde por callback
                }
            },
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun obtenerTextoUbicacion(context: Context, callback: (String) -> Unit) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val loc = result.lastLocation
                    val texto = if (loc != null) {
                        covertLocationToText(loc.latitude, loc.longitude)
                    } else {
                        "No se pudo obtener la ubicación."
                    }

                    // Actualiza el estado si deseas
                    locationText = texto

                    callback(texto)
                }
            },
            Looper.getMainLooper()
        )
    }







    // aqui se verificara si hay internet para actulizar los datos si se cambiarn
    // si no pues no JAJAJA

    fun updateProfile(context: Context, userData: UserData) {
        if (isInternetAvailable(context)) {
            viewModelScope.launch {
                try {
                    MongoUserRepository.updateUser(userData)
                    Log.d("MainViewModel", "Datos sincronizados con backend")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error al sincronizar: ${e.message}")
                }
            }
        } else {
            saveDataLocally(context, userData)
        }
    }

    fun saveDataLocally(context: Context, userData: UserData) {
        saveUserProfile(context, userData)
        saveEmergencyContacts(context, emergencyContacts)
    }

    fun loadLocalData(context: Context) {
        val user = loadUserProfile(context) ?: return
        loadValues(user)
        emergencyContacts = loadEmergencyContacts(context)
    }


    private fun loadValues(user: UserData) {
        name.value = user.name
        lastName.value = user.lastName
        secondLastName.value = user.secondLastName
        email.value = user.email
        telephone.value = user.telephone
        birthDay.value = user.birthDay
        state.value = user.location.state
        municipality.value = user.location.municipality
        colony.value = user.location.colony
        street.value = user.location.street
        emergencyContacts = user.emergencyContacts
        bloodType.value = user.bloodType
        disabilityDescription.value = user.disabilityDescription
    }

    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkAndLoadProfile(context: Context) {
        val email = getUserEmail(context)
        if (email != null && isInternetAvailable(context)) {
            viewModelScope.launch {
                try {
                    Log.d("MainViewModel", "Cargando datos desde backend para: $email")
                    // 1. Obtener perfil
                    val user = MongoUserRepository.getUserByEmail(email)
                    if (user != null) {
                        // Campos básicos
                        name.value = user.name
                        lastName.value = user.lastName
                        secondLastName.value = user.secondLastName
                        this@MainViewModel.email.value = user.email
                        telephone.value = user.telephone
                        birthDay.value = user.birthDay

                        // Dirección
                        state.value = user.location.state
                        municipality.value = user.location.municipality
                        colony.value = user.location.colony
                        street.value = user.location.street

                        // Nuevos datos de perfil
                        bloodType.value = user.bloodType
                        disabilityDescription.value = user.disabilityDescription

                        // ← Cargar contactos de emergencia
                        emergencyContacts = user.emergencyContacts

                        // Guardar localmente perfil y contactos
                        saveUserProfile(context, user)
                        saveEmergencyContacts(context, user.emergencyContacts)
                        Log.d("MainViewModel", "EmgContacts: ${user.emergencyContacts}, BloodType: ${user.bloodType}, Disability: ${user.disabilityDescription}")
                        Log.d("MainViewModel", "✅ Perfil cargado desde backend")
                        sincronizarMedicamentosPendientes(context)
                    } else {
                        Log.e("MainViewModel", "⚠️ Usuario no encontrado en backend")
                    }

                    // 2. Obtener medicamentos del backend
                    val response = RetrofitClient.apiService.getMedications(email)
                    if (response.isSuccessful) {
                        val meds = response.body() ?: emptyList()
                        medicationsUiState = medicationsUiState.copy(medications = meds)
                        saveMedications(context, meds)
                        Log.d("MainViewModel", "✅ Medicamentos sincronizados desde backend")
                    } else {
                        Log.e("MainViewModel", "❌ Error cargando medicamentos: ${response.code()}")
                    }

                } catch (e: Exception) {
                    Log.e("MainViewModel", "❌ Error al cargar datos: ${e.message}")
                    loadLocalData(context)
                    loadMedicationsLocal(context)
                }
            }
        } else {
            Log.d("MainViewModel", "📴 Sin internet. Cargando desde almacenamiento local.")
            loadLocalData(context)
            loadMedicationsLocal(context)
        }
    }



    // aqui se actualizaran los datos por si se desea modificar algo de nuestra vista
    //de aboutme

    fun updateProfileFromDialog(
        name: String,
        lastName: String,
        secondLastName: String,
        birthDay: String,
        telephone: String,
        street: String,
        state: String,
        municipality: String,
        colony: String,
        bloodType: String,
        disabilityDescription: String
    ) {
        this.name.value = name
        this.lastName.value = lastName
        this.secondLastName.value = secondLastName
        this.birthDay.value = birthDay
        this.telephone.value = telephone
        this.street.value = street
        this.state.value = state
        this.municipality.value = municipality
        this.colony.value = colony
        this.bloodType.value = bloodType
        this.disabilityDescription.value = disabilityDescription

        val user = buildUserData()

        updateProfile(appContext, user)
    }


    fun buildUserData(): UserData {
        return UserData(
            name = name.value,
            lastName = lastName.value,
            secondLastName = secondLastName.value,
            email = email.value,
            telephone = telephone.value,
            birthDay = birthDay.value,
            location = Location(
                state = state.value,
                municipality = municipality.value,
                colony = colony.value,
                street = street.value
            ),
            bloodType = bloodType.value,
            disabilityDescription = disabilityDescription.value,
            emergencyContacts = emergencyContacts
        )
    }

    fun deleteAccount(context: Context) {
        viewModelScope.launch {
            try {
                val email = this@MainViewModel.email.value
                val response = RetrofitClient.apiService.deleteUserByEmail(email)
                if (response.isSuccessful) {
                    clearUserPrefs(context) // limpia SharedPreferences
                    println("✅ Cuenta eliminada correctamente")
                } else {
                    println("❌ No se pudo eliminar: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // esta parte se refiere a la logica de los medicamentos

    fun addMedication(nombre: String, description: String) {
        val email = this@MainViewModel.email.value  // asegúrate que ya está inicializado antes
        val new = Medications(nombre, description, email)
        val newList = medicationsUiState.medications + new
        medicationsUiState = medicationsUiState.copy(medications = newList)

        saveMedicationsLocal(appContext)
    }


    fun saveMedicationsLocal(context: Context) {
        saveMedications(context, medicationsUiState.medications)
    }

    fun loadMedicationsLocal(context: Context) {
        val loadMedications = loadMedications(context)
        medicationsUiState = medicationsUiState.copy(medications = loadMedications)
    }


    fun addMedicationsBackend(context: Context, name: String, description: String) {
        val email = this@MainViewModel.email.value
        if (email.isBlank()) {
            Log.e("MainViewModel", "❌ No se puede agregar: email vacío")
            Toast.makeText(context, "⚠️ Error: tu correo no está cargado aún", Toast.LENGTH_SHORT).show()
            return
        }

        val newMedications = Medications(name, description, email)
        Log.d("MainViewModel", "📦 Enviando medicamento: $newMedications")

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addMedicamento(newMedications)
                if (response.isSuccessful) {
                    loadMedicationsFromBackend(context)
                    Log.d("MainViewModel", "📧 Email actual: '${email}'")
                    Log.d("MainViewModel", "✅ Medicamento agregado")
                } else {
                    Log.d("MainViewModel", "📧 Email actual: '${email}'")
                    Log.e("MainViewModel", "❌ Error al guardar medicamento: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "📧 Email actual: '${email}'")
                Log.e("MainViewModel", "❌ Error al agregar medicamento: ${e.message}")
            }
        }
    }

    fun loadMedicationsFromBackend(context: Context) {
        val email = this@MainViewModel.email.value
        if (email.isBlank()) {
            Log.e("MainViewModel", "❌ No se puede cargar: email vacío")
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMedications(email)
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    medicationsUiState = medicationsUiState.copy(medications = list)
                    saveMedications(context, list)
                    Log.d("MainViewModel", "📦 Lista recibida del backend: $list")
                    Log.d("MainViewModel", "✅ Medicamentos cargados desde backend")
                } else {
                    Log.e("MainViewModel", "❌ Error al cargar medicamentos: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error al obtener medicamentos: ${e.message}")
            }
        }
    }



    fun editMedicationsBackend(context: Context, medications: Medications, newDescriptions: String) {
        val updateMedications = medications.copy(description = newDescriptions)

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateMedicamento(updateMedications._id!!, updateMedications)
                if (response.isSuccessful) {
                    val newList = medicationsUiState.medications.map {
                        if (it._id == updateMedications._id) updateMedications else it
                    }
                    medicationsUiState = medicationsUiState.copy(medications = newList)
                    saveMedications(context, newList)
                    Log.d("MainViewModel", "✅ Medicamento editado")
                } else {
                    Log.e("MainViewModel", "❌ Error al editar medicamento")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error de red al editar: ${e.message}")
            }
        }
    }

    fun deleteMedications(context: Context, medications: Medications) {
        val id = medications._id
        if (id.isNullOrBlank()) {
            Log.e("MainViewModel", "❌ No se puede eliminar: ID nulo")
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteMedications(id)
                if (response.isSuccessful) {
                    val medicationsUpdate = medicationsUiState.medications.filterNot { it._id == id }
                    medicationsUiState = medicationsUiState.copy(medications = medicationsUpdate)
                    saveMedications(context, medicationsUpdate)
                    Log.d("MainViewModel", "✅ Medicamento eliminado del backend y local")
                } else {
                    Log.e("MainViewModel", "❌ Falló al eliminar medicamento: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error eliminando medicamento: ${e.message}")
            }
        }
    }

    fun cleanInvalidMedications(context: Context) {
        val cleaned = medicationsUiState.medications.filter {
            it.name.isNotBlank() || it.description.isNotBlank()
        }

        medicationsUiState = medicationsUiState.copy(medications = cleaned)
        saveMedications(context, cleaned)
        Log.d("MainViewModel", "🧹 Medicamentos inválidos eliminados localmente")
    }

    private suspend fun sincronizarMedicamentosPendientes(context: Context) {
        val email = this@MainViewModel.email.value
        if (email.isBlank()) return

        val pendientes = medicationsUiState.medications.filter { it._id == null }

        for (med in pendientes) {
            try {
                val nuevo = med.copy(email = email) // Asegura que tenga el email
                val response = RetrofitClient.apiService.addMedicamento(nuevo)
                if (response.isSuccessful) {
                    Log.d("MainViewModel", "✅ Medicamento sincronizado: ${med.name}")
                } else {
                    Log.e("MainViewModel", "❌ Error al sincronizar ${med.name}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "❌ Error al sincronizar ${med.name}: ${e.message}")
            }
        }

        // Recarga los medicamentos desde backend para actualizar _id y evitar duplicados
        loadMedicationsFromBackend(context)
    }



    // Esta parta sera para las funciones tactiles de desbloquear pantalla o no

    fun navegarARutaPorVoz(ruta: String) {
        rutaComando = ruta
    }
    fun resetRutaPorVoz() {
        rutaComando = null
    }

    fun setPerfilCargado(valor: Boolean) {
        appUiState = appUiState.copy(perfilCargado = valor)
    }


    fun setLocked(context: Context, locked: Boolean, hablarDespedida: Boolean = true) {
        appUiState = appUiState.copy(isLocked = locked)

        if (locked) {
            if (!appUiState.perfilCargado) {
                checkAndLoadProfile(context)
                setPerfilCargado(true)
            }

            playSound(context, R.raw.activate)

            TextToSpeechUtils.iniciarTTS(context) {
                val nombreUsuario = name.value.ifBlank { "amigo" }

                // 🚫 Detén antes de hablar
                VoskRecognizerUtils.stopRecognition()

                // 🗣️ Habla y luego activa escucha
                TextToSpeechUtils.hablarConCallback(
                    texto = "Hola $nombreUsuario, estoy escuchando tus comandos",
                    utteranceId = "GREETING"
                ) {
                    VoskRecognizerUtils.startRecognition(context, this@MainViewModel)
                }
            }
        } else {
            playSound(context, R.raw.activate)

            TextToSpeechUtils.iniciarTTS(context) {
                VoskRecognizerUtils.stopRecognition()

                if (hablarDespedida) {
                    TextToSpeechUtils.hablarConCallback(
                        texto = "Hasta luego",
                        utteranceId = "FAREWELL"
                    ) {
                        TextToSpeechUtils.liberar()
                    }
                } else {
                    TextToSpeechUtils.liberar()
                }
            }
        }
    }



    fun playSound(context: Context, soundResId: Int) {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    fun actualizarTextoReconocido(texto: String) {
        recognizedTextVoice = texto
    }



    // esta parte esta dedicada al generador de QR
    //
    fun cargarUserIdDesdeBackend() {
        val correo = email.value.ifBlank {
            getUserEmail(appContext) ?: run {
                Log.e("MainViewModel", "❌ No se pudo cargar el correo desde preferencias ni desde memoria.")
                return
            }
        }

        Log.d("MainViewModel", "📧 Solicitando userId con correo: $correo")

        viewModelScope.launch {
            val id = MongoUserRepository.getUserId(correo)
            userId = id
            if (id != null) {
                Log.d("MainViewModel", "✅ ID recibido: $id")
                saveUserId(appContext, id)
            } else {
                Log.e("MainViewModel", "❌ No se pudo obtener el ID del backend")
            }
        }
    }


    // esta seccion es para los contanctos de emergencia, proximamente se cambiara
    // ya que el codigo esta mal ordenado
    fun addEmergencyContact(contact: EmergencyContact) {
        if (emergencyContacts.size >= 3) return
        emergencyContacts = emergencyContacts + contact
        syncContactsWithProfile()
    }

    fun updateEmergencyContact(index: Int, updatedContact: EmergencyContact) {
        if (index !in emergencyContacts.indices) return
        emergencyContacts = emergencyContacts.toMutableList().also { it[index] = updatedContact }
        syncContactsWithProfile()
    }

    fun deleteEmergencyContact(index: Int) {
        if (index !in emergencyContacts.indices) return
        emergencyContacts = emergencyContacts.toMutableList().also { it.removeAt(index) }
        syncContactsWithProfile()
    }

    private fun syncContactsWithProfile() {
        val updatedUser = buildUserData().copy(emergencyContacts = emergencyContacts)
        updateProfile(appContext, updatedUser)
        saveEmergencyContacts(appContext, emergencyContacts)
    }

    //esta parte esta destinada para mandar mensajes SOS para los contactos de emergencia con twilo
    // tendra un limite de 3 veces por cada 10 min


    fun sendSmsSOS(
        telefonos: List<String>,
        mensaje: String,
        onResultado: (Boolean) -> Unit
    ) {
        val ahora = System.currentTimeMillis()

        Log.d("SOS", "🕒 Tiempo actual: $ahora")
        Log.d("SOS", "📱 Teléfonos recibidos: $telefonos")
        Log.d("SOS", "✉️ Mensaje: $mensaje")

        // Si es el primer intento, guarda el tiempo
        if (tiempoPrimerSos == 0L) {
            tiempoPrimerSos = ahora
            Log.d("SOS", "⏱️ Inicializando tiempo del primer SOS")
        }

        val diezMinutosMs = 10 * 60 * 1000
        if (ahora - tiempoPrimerSos > diezMinutosMs) {
            Log.d("SOS", "🔄 Más de 10 min pasados. Reiniciando contador de intentos.")
            intentosSos = 0
            tiempoPrimerSos = ahora
        }

        if (intentosSos >= 3) {
            Log.w("SOS", "🚫 Límite alcanzado de intentos SOS.")
            Toast.makeText(appContext, "❌ Límite de 3 SOS cada 10 minutos alcanzado.", Toast.LENGTH_LONG).show()
            onResultado(false)
            return
        }

        viewModelScope.launch {
            try {
                val request = SosRequest(to = telefonos, message = mensaje)
                val response = RetrofitClient.apiService.enviarSOS(request)

                Log.d("SOS", "📡 Enviando request al backend: $request")




                if (response.isSuccessful) {
                    intentosSos++
                    val restantes = 3 - intentosSos
                    Log.d("SOS", "✅ SOS enviado. Intentos usados: $intentosSos, restantes: $restantes")
                    Toast.makeText(appContext, "✅ SOS enviado. Intentos restantes: $restantes", Toast.LENGTH_LONG).show()
                    onResultado(true)
                } else {
                    Log.e("SOS", "❌ Error respuesta backend: ${response.code()}")
                    onResultado(false)
                }
            } catch (e: Exception) {
                Log.e("SOS", "❌ Excepción al enviar SOS: ${e.message}")
                onResultado(false)
            }
        }
    }

    fun getLinkLocation(callback: (String) -> Unit) {
        val permiso = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(appContext, permiso) != PackageManager.PERMISSION_GRANTED) {
            callback("Permiso de ubicación no concedido.")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val loc = result.lastLocation
                    if (loc != null) {
                        val link = "https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
                        callback(link)
                    } else {
                        callback("No se pudo obtener la ubicación.")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    // seccion dedicada al token de vinculacion

    private fun generateToken(): String {
        return (100000..999999).random().toString()
    }

    fun startTokenGeneration(onUpdate: (String) -> Unit) {
        tokenJob?.cancel()
        tokenJob = viewModelScope.launch {
            while (true) {
                val nuevoToken = generateToken()
                currentToken = nuevoToken
                Log.d("Token", "🎲 Token generado: $nuevoToken")
                onUpdate(nuevoToken)

                viewModelScope.launch {
                    try {
                        var emailActual = email.value

                        if (emailActual.isBlank()) {
                            // Intentamos cargar desde SharedPreferences
                            emailActual = getUserEmail(appContext).orEmpty()
                            if (emailActual.isNotBlank()) {
                                Log.d("Token", "📥 Email cargado desde preferencias: $emailActual")
                                email.value = emailActual // opcionalmente lo guardas
                            } else {
                                Log.e("Token", "❌ No se pudo obtener el email ni desde memoria ni desde preferencias")
                            }
                        }

                        if (emailActual.isNotBlank()) {
                            Log.d("Token", "📨 Enviando token al backend: $nuevoToken para $emailActual")
                            val response = RetrofitClient.apiService.actualizarTokenVinculacion(
                                mapOf("email" to emailActual, "linkToken" to nuevoToken)
                            )

                            if (response.isSuccessful) {
                                Log.d("Token", "✅ Token actualizado correctamente en el backend")
                            } else {
                                Log.e("Token", "❌ Error al guardar token: ${response.code()} - ${response.message()}")
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("Token", "❌ Excepción al enviar token: ${e.message}")
                    }
                }

                delay(5 * 60 * 1000)
            }
        }
    }













}
