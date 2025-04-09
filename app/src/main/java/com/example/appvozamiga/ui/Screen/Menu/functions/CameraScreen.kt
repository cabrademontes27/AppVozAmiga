package com.example.appvozamiga.ui.Screen.Menu.functions

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.appvozamiga.ui.Navigation.Routes
import com.example.appvozamiga.R

@Composable
fun CameraScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de Cámara", fontSize = 20.sp)
        }
        BottomBar(navController = navController)
    }
}