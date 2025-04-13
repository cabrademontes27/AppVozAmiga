package com.example.appvozamiga.viewModels.menu

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvozamiga.data.models.UserData
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
import com.example.appvozamiga.data.models.Location as UserLocation



class MainViewModel(application: Application) : AndroidViewModel(application) {
    val appContext = application.applicationContext

    var recognizedText by mutableStateOf("Esperando captura...")
        private set

    var locationText by mutableStateOf("Obteniendo ubicación...")
        private set

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



    //aqui es solo la parte visual de funcion de la camara,
    //procesar, limpiar y filtrar texto

    fun processImage(bitmap: Bitmap) {
        TextRecognitionUtils.recognizeTextFromBitmap(
            bitmap,
            onSuccess = {
                recognizedText = cleanText(it)
            },
            onFailure = {
                recognizedText = "Error: ${it.localizedMessage}"
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
    fun obtenerUbicacion() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val location: Location? = result.lastLocation
                    location?.let {
                        convertirUbicacionATexto(it.latitude, it.longitude)
                    } ?: run {
                        locationText = "No se pudo obtener la ubicación."
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    // 🗺️ Convertir coordenadas a texto entendible
    private fun convertirUbicacionATexto(lat: Double, lon: Double) {
        val geocoder = Geocoder(getApplication(), Locale.getDefault())
        try {
            val direccion = geocoder.getFromLocation(lat, lon, 1)
            if (!direccion.isNullOrEmpty()) {
                val dir = direccion[0]
                val estado = dir.adminArea ?: ""
                val ciudad = dir.locality ?: dir.subAdminArea ?: ""
                val colonia = dir.subLocality ?: ""
                val calle = dir.thoroughfare ?: ""

                locationText = "Estás en $estado, $ciudad en la colonia $colonia en la calle $calle"
            } else {
                locationText = "No se encontró la dirección."
            }
        } catch (e: Exception) {
            locationText = "Error al obtener la dirección: ${e.message}"
        }
    }

    // aqui se verificara si hay internet para actulizar los datos si se cambiarn
    // si no pues no JAJAJA

    fun actualizarPerfil(context: Context, userData: UserData) {
        if (hayInternetDisponible(context)) {
            viewModelScope.launch {
                try {
                    MongoUserRepository.updateUser(userData)
                    Log.d("MainViewModel", "Datos sincronizados con backend")
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error al sincronizar: ${e.message}")
                }
            }
        } else {
            guardarDatosLocalmente(context, userData)
        }
    }

    fun guardarDatosLocalmente(context: Context, userData: UserData) {
        saveUserProfile(context, userData)
    }

    fun cargarDatosGuardados(context: Context) {
        val user = loadUserProfile(context) ?: return
        cargarValoresDesde(user)
    }


    private fun cargarValoresDesde(user: UserData) {
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
    }

    fun hayInternetDisponible(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkAndLoadProfile(context: Context) {
        val correo = getUserEmail(context)
        if (correo != null && hayInternetDisponible(context)) {
            viewModelScope.launch {
                try {
                    Log.d("MainViewModel", "Buscando usuario con correo: $correo")

                    val user = MongoUserRepository.getUserByEmail(correo)
                    if (user != null) {
                        // Guardar en memoria
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

                        // Guardar en local
                        saveUserProfile(context, user)

                        Log.d("MainViewModel", "✅ Datos cargados desde backend")
                    } else {
                        Log.e("MainViewModel", "⚠️ Usuario no encontrado en backend")
                        cargarDatosGuardados(context)
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "❌ Error obteniendo usuario: ${e.message}")
                    cargarDatosGuardados(context)
                }
            }
        } else {
            Log.d("MainViewModel", "🌐 Sin conexión o sin correo guardado")
            cargarDatosGuardados(context)
        }
    }

    // aqui se actualizaran los datos por si se desea modificar algo de nuestra vista
    //de aboutme

    fun actualizarDatosDesdeDialogo(
        name: String,
        lastName: String,
        secondLastName: String,
        birthDay: String,
        telephone: String,
        state: String,
        municipality: String,
        colony: String,
        street: String
    ) {
        this.name.value = name
        this.lastName.value = lastName
        this.secondLastName.value = secondLastName
        this.birthDay.value = birthDay
        this.telephone.value = telephone
        this.state.value = state
        this.municipality.value = municipality
        this.colony.value = colony
        this.street.value = street

        val user = buildUserData() // Tu método para crear un UserData con todos los campos

        saveUserProfile(appContext, user)

        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateUser(user) // esto lo armamos después
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun buildUserData(): UserData {
        return UserData(
            name = name.value,
            lastName = lastName.value,
            secondLastName = secondLastName.value,
            email = email.value,
            telephone = telephone.value,
            birthDay = birthDay.value,
            location = UserLocation(
                state = state.value,
                municipality = municipality.value,
                colony = colony.value,
                street = street.value
            )
        )
    }







}
