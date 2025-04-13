package com.example.appvozamiga.viewModels.menu

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import com.example.appvozamiga.utils.TextRecognitionUtils
import java.util.Locale
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var recognizedText by mutableStateOf("Esperando captura...")
        private set

    var locationText by mutableStateOf("Obteniendo ubicación...")
        private set

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)


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
}
