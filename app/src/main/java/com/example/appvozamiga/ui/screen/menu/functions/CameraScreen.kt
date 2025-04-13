package com.example.appvozamiga.ui.screen.menu.functions

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.viewModels.menu.MainViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {
    val mainViewModel: MainViewModel = viewModel()
    val recognizedText = mainViewModel.recognizedText

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val cameraPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted.value = granted
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted.value) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var showSnackbar by remember { mutableStateOf(false) }

    if (showSnackbar) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Medicamento agregado automáticamente", Toast.LENGTH_SHORT).show()
            showSnackbar = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (cameraPermissionGranted.value) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val previewUseCase = Preview.Builder().build()
                                val captureUseCase = ImageCapture.Builder().build()
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        previewUseCase,
                                        captureUseCase
                                    ).also {
                                        previewUseCase.setSurfaceProvider(previewView.surfaceProvider)
                                        imageCapture = captureUseCase
                                    }
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Error al iniciar la cámara", e)
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val imageCaptureInstance = imageCapture ?: return@Button

                        imageCaptureInstance.takePicture(
                            executor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    val bitmap = imageProxy.toBitmap()
                                    if (bitmap != null) {
                                        mainViewModel.processImage(bitmap)
                                        val texto = mainViewModel.recognizedText
                                        if (texto.isNotBlank()) {
                                            mainViewModel.agregarMedicamento(
                                                nombre = texto,
                                                descripcion = "Detectado con cámara"
                                            )
                                            showSnackbar = true
                                        }
                                    }
                                    imageProxy.close()
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraScreen", "Error al capturar: ${exception.message}")
                                }
                            }
                        )
                    }) {
                        Text("Capturar")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Texto detectado:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = recognizedText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Text(
                    text = "Se requiere permiso de cámara para usar esta función.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }

        BottomBar(navController = navController)
    }
}
