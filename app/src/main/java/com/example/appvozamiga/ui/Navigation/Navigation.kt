package com.example.appvozamiga.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appvozamiga.ViewModels.RegisterViewModel
import com.example.appvozamiga.repository.fireBase.isUserRegistered
import com.example.appvozamiga.ui.Screen.Menu.MainMenuScreen
import com.example.appvozamiga.ui.Screen.register.RegisterScreen
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
            val registerViewModel: RegisterViewModel = viewModel()
            val state = registerViewModel.uiState.value

            when {
                state.isLoading -> {
                    LoadingScreen()
                }
                state.isSuccess -> {
                    SuccessScreen(onFinish = {
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    })
                }
                else -> {
                    RegisterScreen(viewModel = registerViewModel) {
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
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
            // Esta sería la pantalla principal de la app
            MainMenuScreen()
        }
    }
}
