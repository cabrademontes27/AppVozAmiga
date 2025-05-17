package com.example.appvozamiga.utils


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.appvozamiga.R
import com.example.appvozamiga.data.models.UbicacionRequest
import com.example.appvozamiga.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.google.android.gms.location.*

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val userEmail by lazy {
        getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getString("email_for_signin", "") ?: ""
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        crearCanalNotificaciones()
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L // cada 10 segundos
        ).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, crearNotificacion())
        iniciarUbicacion()
        return START_STICKY
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ubicacion_channel",
                "Ubicación en segundo plano",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun crearNotificacion(): Notification {
        return NotificationCompat.Builder(this, "ubicacion_channel")
            .setContentTitle("Voz Amiga")
            .setContentText("Enviando tu ubicación en tiempo real...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun iniciarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf()
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc: Location? = result.lastLocation
                    if (loc != null && userEmail.isNotBlank()) {
                        enviarUbicacion(loc.latitude, loc.longitude)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun enviarUbicacion(lat: Double, lon: Double) {
        job?.cancel()
        job = scope.launch {
            try {
                val ubicacion = UbicacionRequest(
                    email = userEmail,
                    lat = lat,
                    lon = lon
                )

                val response = RetrofitClient.apiService.actualizarUbicacion(ubicacion)

                if (response.isSuccessful) {
                    println("✅ Ubicación enviada: $lat, $lon")
                } else {
                    println("❌ Error al enviar ubicación: ${response.code()}")
                }
            } catch (e: Exception) {
                println("❌ Excepción al enviar ubicación: ${e.message}")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
