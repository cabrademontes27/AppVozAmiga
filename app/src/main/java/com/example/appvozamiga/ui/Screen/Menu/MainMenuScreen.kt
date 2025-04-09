package com.example.appvozamiga.ui.Screen.Menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appvozamiga.R


//empezar a recrear la imagen de todo lo que contendra el mainMenu

data class MenuItem(val title: String, val icon: Painter)

@Composable
fun MainMenuScreen() {
    val menuItems = listOf(
        MenuItem("Medicamentos", painterResource(R.drawable.ic_launcher_foreground)),
        MenuItem("Ubicación", painterResource(R.drawable.ic_launcher_foreground)),
        MenuItem("Cámara", painterResource(R.drawable.ic_launcher_foreground)),
        MenuItem("Asistente", painterResource(R.drawable.ic_launcher_foreground)),
        // Agrega más si necesitas
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE4EEFF)) // fondo general
            .padding(16.dp)
    ) {
        Text(
            text = "Voz Amiga",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA4937F),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        )

        Text(
            text = "Bienvenido/a",
            fontSize = 18.sp,
            color = Color(0xFFA4937F),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems) { item ->
                MenuButton(item)
            }
        }
    }
}

@Composable
fun MenuButton(item: MenuItem) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color(0xFFA4937F)
            )
        }
    }
}

