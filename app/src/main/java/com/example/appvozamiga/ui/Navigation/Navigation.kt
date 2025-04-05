package com.example.appvozamiga.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appvozamiga.ui.Screen.register.RegisterScreen
import com.example.appvozamiga.ui.Screen.register.VerificationScreen
import com.example.appvozamiga.ui.Screen.splash.SplashScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Routes.REGISTER) {
                        // Esto evita que el usuario pueda volver atrás al Splash
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(viewModel = viewModel()) {
                navController.navigate(Routes.VERIFICATION)
            }
        }

        composable(Routes.VERIFICATION) {
            VerificationScreen {
                navController.navigate("mainMenu") {
                    popUpTo(Routes.VERIFICATION) { inclusive = true }
                }
            }
        }



    }
}

