package com.example.appvozamiga.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appvozamiga.ViewModels.RegisterViewModel
import com.example.appvozamiga.repository.mongodb.isUserRegistered
import com.example.appvozamiga.ui.Screen.Menu.MainMenuScreen
import com.example.appvozamiga.ui.Screen.Menu.functions.AboutMeScreen
import com.example.appvozamiga.ui.Screen.Menu.functions.CameraScreen
import com.example.appvozamiga.ui.Screen.Menu.functions.DrugsScreen
import com.example.appvozamiga.ui.Screen.Menu.functions.UbicationScreen
import com.example.appvozamiga.ui.Screen.register.RegisterScreen
import com.example.appvozamiga.ui.Screen.splash.LoadingRedirectScreen
import com.example.appvozamiga.ui.Screen.splash.LoadingScreen
import com.example.appvozamiga.ui.Screen.splash.SplashScreen
import com.example.appvozamiga.ui.Screen.splash.SuccessScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            val context = LocalContext.current
            SplashScreen()

            LaunchedEffect(Unit) {
                delay(2000)
                if (isUserRegistered(context)) {
                    navController.navigate(Routes.MAIN_MENU) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.REGISTER) {
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = viewModel()
            val state = registerViewModel.uiState.value

            LaunchedEffect(Unit) {
                registerViewModel.verificarEstadoDesdeBackend(context)
            }

            RegisterScreen(viewModel = registerViewModel) {}

            if (state.isLoading) {
                LoadingScreen()
            }

            if (state.isVerified) {
                SuccessScreen(onFinish = {})
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


        composable(Routes.SUCCESS) {
            SuccessScreen(onFinish = {
                navController.navigate(Routes.MAIN_MENU) {
                    popUpTo(Routes.SUCCESS) { inclusive = true }
                }
            })
        }

        composable(Routes.MAIN_MENU) {
            MainMenuScreen(navController = navController)
        }

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

        composable(Routes.DRUGS) {
            DrugsScreen(navController)
        }
        composable(Routes.LOCATION) {
            UbicationScreen(navController)
        }
        composable(Routes.CAMERA) {
            CameraScreen(navController)
        }
        composable(Routes.ABOUT_ME) {
            AboutMeScreen(navController)
        }
    }
}
