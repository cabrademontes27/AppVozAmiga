package com.example.appvozamiga.ui.screen.menu.functions

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp


@Composable
fun AboutMeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla Acerca de Mí", fontSize = 20.sp)
        }
        BottomBar(navController = navController)
    }
}

// no entran las pantallassasss ahhhhhhhhhh