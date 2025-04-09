package com.example.appvozamiga.ui.Screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.appvozamiga.ui.Navigation.Routes


@Composable
fun LoadingRedirectScreen(
    navController: NavController,
    destinationRoute: String
) {
    SplashScreen() // o tu pantalla de carga visual

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500)
        navController.navigate(destinationRoute) {
            popUpTo(Routes.MAIN_MENU) // o ajusta según tu flujo
        }
    }
}