package com.example.appvozamiga.ui.screen.menu.functions

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.utils.shareBitmap
import com.example.appvozamiga.viewModels.menu.MainViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay

@Composable
fun QrScreen(navController: NavController, mainViewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val userId = mainViewModel.userId
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val retryCount = remember { mutableStateOf(0) }

    LaunchedEffect(retryCount.value) {
        if (mainViewModel.userId == null && retryCount.value < 3) {
            if (retryCount.value > 0) delay(3000) // Solo espera en reintentos
            mainViewModel.cargarUserIdDesdeBackend()
            retryCount.value += 1
        }
    }



    // Generar QR al tener userId
    LaunchedEffect(userId) {
        if (qrBitmap == null && userId != null) {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(
                "https://webvozamiga.vercel.app/info/$userId",
                BarcodeFormat.QR_CODE,
                512,
                512
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            qrBitmap = bitmap
        }
    }


    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Código QR personal",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (userId != null) {
                Text(
                    text = "Este QR está vinculado a tu cuenta.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                qrBitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "Código QR")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        shareBitmap(context, it)
                    }) {
                        Text("Compartir QR")
                    }
                } ?: Text("Generando tu código QR...")
            } else {
                Text("Cargando tu identificación...")
            }
        }
    }
}