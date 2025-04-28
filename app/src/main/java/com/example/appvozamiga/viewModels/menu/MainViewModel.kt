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
import com.example.appvozamiga.data.models.Location as UserLocation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.appvozamiga.data.models.Medications
import com.example.appvozamiga.data.models.loadMedications
import com.example.appvozamiga.data.models.saveMedications


class MainViewModel(application: Application) : AndroidViewModel(application) {
    val appContext = application.applicationContext

    var recognizedText by mutableStateOf("")
        private set

    var locationText by mutableStateOf("Obteniendo ubicación...")
        private set

    var medicationsUiState by mutableStateOf(DrugsUiState())
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
    fun getLocation() {
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
                        covertLocationToText(it.latitude, it.longitude)
                    } ?: run {
                        locationText = "No se pudo obtener la ubicación."
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    // 🗺️ Convertir coordenadas a texto entendible
    private fun covertLocationToText(lat: Double, lon: Double) {
        val geocoder = Geocoder(getApplication(), Locale.getDefault())
        try {
            val location = geocoder.getFromLocation(lat, lon, 1)
            if (!location.isNullOrEmpty()) {
                val dir = location[0]
                val state = dir.adminArea ?: ""
                val city = dir.locality ?: dir.subAdminArea ?: ""
                val colony = dir.subLocality ?: ""
                val street = dir.thoroughfare ?: ""

                locationText = "Estás en $state, $city en la colonia $colony en la calle $street"
            } else {
                locationText = "No se encontró la dirección."
            }
        } catch (e: Exception) {
            locationText = "Error al obtener la dirección: ${e.message}"
        }
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
    }

    fun loadLocalData(context: Context) {
        val user = loadUserProfile(context) ?: return
        loadValues(user)
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
                    Log.d("MainViewModel", "Buscando usuario con correo: $email")

                    val user = MongoUserRepository.getUserByEmail(email)
                    if (user != null) {
                        // Guardar en memoria
                        name.value = user.name
                        lastName.value = user.lastName
                        secondLastName.value = user.secondLastName
                        this@MainViewModel.email.value = user.email
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
                        loadLocalData(context)
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "❌ Error obteniendo usuario: ${e.message}")
                    loadLocalData(context)
                }
            }
        } else {
            Log.d("MainViewModel", "🌐 Sin conexión o sin correo guardado")
            loadLocalData(context)
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

        val user = buildUserData() // metodo para crear un UserData con todos los campos

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
            location = UserLocation(
                state = state.value,
                municipality = municipality.value,
                colony = colony.value,
                street = street.value
            )
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

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addMedicamento(newMedications)
                if (response.isSuccessful) {
                    loadMedicationsFromBackend(context)
                    Log.d("MainViewModel", "✅ Medicamento agregado")
                } else {
                    Log.e("MainViewModel", "❌ Error al guardar medicamento: ${response.code()}")
                }
            } catch (e: Exception) {
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


}
