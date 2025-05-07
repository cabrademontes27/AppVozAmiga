package com.example.appvozamiga.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appvozamiga.viewModels.register.RegisterViewModel
import com.example.appvozamiga.data.models.isUserRegistered
import com.example.appvozamiga.ui.screen.menu.MainMenuScreen
import com.example.appvozamiga.ui.screen.menu.functions.AboutMeScreen
import com.example.appvozamiga.ui.screen.menu.functions.CameraScreen
import com.example.appvozamiga.ui.screen.menu.functions.DrugsScreen
import com.example.appvozamiga.ui.screen.menu.functions.UbicationScreen
import com.example.appvozamiga.ui.screen.register.RegisterScreen
import com.example.appvozamiga.ui.screen.splash.LoadingRedirectScreen
import com.example.appvozamiga.ui.screen.splash.LoadingScreen
import com.example.appvozamiga.ui.screen.splash.SplashScreen
import com.example.appvozamiga.ui.screen.splash.SuccessScreen
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.example.appvozamiga.viewModels.menu.MainViewModel
import com.example.appvozamiga.ui.screen.blocked.LockOverlay
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.appvozamiga.ui.screen.login.LoginScreen
import com.example.appvozamiga.data.models.getUserEmail
import com.example.appvozamiga.data.models.saveSignInEmail
import com.example.appvozamiga.data.models.setUserRegistered
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import com.example.appvozamiga.ui.screen.menu.functions.QrScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val isLocked by remember { derivedStateOf { mainViewModel.appUiState.isLocked } }
    val context = LocalContext.current

    // Mic permission launcher
    var micPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val micLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        micPermissionGranted = granted
        if (!granted) {
            Toast.makeText(
                context,
                "Se necesita permiso de micrófono para comandos de voz",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Triple/quad tap to lock
    val tapTimes = remember { mutableStateListOf<Long>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.previousPressed && !it.pressed }) {
                            val now = System.currentTimeMillis()
                            tapTimes.add(now)
                            tapTimes.removeAll { now - it > 800 }

                            when (tapTimes.size) {
                                3 -> {
                                    if (!isLocked) {
                                        if (micPermissionGranted) {
                                            mainViewModel.setLocked(context, true)
                                        } else {
                                            micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                                4 -> {
                                    if (isLocked) {
                                        mainViewModel.setLocked(context, false)
                                    }
                                    tapTimes.clear()
                                }
                            }
                        }
                    }
                }
            }
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH
        ) {
            // Splash decides next screen
            composable(Routes.SPLASH) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    delay(2000)
                    if (isUserRegistered(context)) {
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                }
            }

            // Register flow
            composable(Routes.REGISTER) {
                val registerViewModel: RegisterViewModel = viewModel()
                val state by registerViewModel.uiState

                // Check existing state on start
                LaunchedEffect(Unit) {
                    registerViewModel.verificarEstadoDesdeBackend(context)
                }

                RegisterScreen(
                    viewModel = registerViewModel,
                    onRegisterComplete = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                )

                if (state.isLoading) LoadingScreen()
                if (state.isVerified) {
                    SuccessScreen(onFinish = {
                        registerViewModel.resetNavigationFlag()
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    })
                }
                LaunchedEffect(state.shouldNavigateToMenu) {
                    if (state.shouldNavigateToMenu) {
                        registerViewModel.resetNavigationFlag()
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                }
            }

            // Login flow
            composable(Routes.LOGIN) {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        // Persist login state
                        setUserRegistered(context)
                        getUserEmail(context)?.let { saveSignInEmail(context, it) }
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOADING_TO_QR) {
                LoadingRedirectScreen(navController, Routes.QR)
            }
            composable(Routes.QR) {
                QrScreen(navController = navController)
            }



            // Main menu
            composable(Routes.MAIN_MENU) {
                MainMenuScreen(navController)
            }

            // Functional screens
            composable(Routes.DRUGS) { DrugsScreen(navController) }
            composable(Routes.LOCATION) { UbicationScreen(navController) }
            composable(Routes.CAMERA) { CameraScreen(navController) }
            composable(Routes.ABOUT_ME) { AboutMeScreen(navController) }

            // Loading redirects
            composable(Routes.LOADING_TO_DRUGS) {
                LoadingRedirectScreen(navController, Routes.DRUGS)
            }
            composable(Routes.LOADING_TO_LOCATION) {
                LoadingRedirectScreen(navController, Routes.LOCATION)
            }
            composable(Routes.LOADING_TO_CAMERA) {
                LoadingRedirectScreen(navController, Routes.CAMERA)
            }
            composable(Routes.LOADING_TO_ABOUT_ME) {
                LoadingRedirectScreen(navController, Routes.ABOUT_ME)
            }
        }

        // Voice-navigation side effect
        LaunchedEffect(mainViewModel.rutaComando) {
            mainViewModel.rutaComando?.let { ruta ->
                navController.navigate(ruta)
                mainViewModel.resetRutaPorVoz()
            }
        }

        // Lock overlay
        if (isLocked) {
            LockOverlay()
        }
    }
}
