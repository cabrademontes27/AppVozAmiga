package com.example.appvozamiga.ui.screen.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.appvozamiga.R
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appvozamiga.ui.navigation.Routes
import com.example.appvozamiga.viewModels.menu.MainViewModel


//empezar a recrear la imagen de todo lo que contendra el mainMenu
data class MenuItem(
    val title: String,
    val icon: Painter,
    val color: Color
)

@Composable
fun MainMenuScreen(navController: NavController) {
    val menuItems = listOf(
        MenuItem("Medicamentos", painterResource(R.drawable.icon_medicament), Color(0xFF5D9CEC)),
        MenuItem("¿Dónde estoy?", painterResource(R.drawable.icons_ubicacion), Color(0xFF4BC1A5)),
        MenuItem("Leer medicamentos", painterResource(R.drawable.camara_icon), Color(0xFFF6BB42)),
        MenuItem("Acerca de mi", painterResource(R.drawable.profile), Color(0xFFE9573F)),
        MenuItem("Generar QR", painterResource(R.drawable.qr), Color(0xFF9B59B6))
    )

    // Mapa para rutas
    val navigationMap = mapOf(
        "Medicamentos" to Routes.LOADING_TO_DRUGS,
        "¿Dónde estoy?" to Routes.LOADING_TO_LOCATION,
        "Leer medicamentos" to Routes.LOADING_TO_CAMERA,
        "Acerca de mi" to Routes.LOADING_TO_ABOUT_ME,
        "Generar QR" to Routes.LOADING_TO_QR
    )

    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo), // Luego agregamos algo
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Voz Amiga",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Bienvenido/a",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF636E72)
                )
            }

            // Grid de botones
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                items(menuItems) { item ->
                    MenuButton(item) {
                        navigationMap[item.title]?.let { route ->
                            navController.navigate(route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButton(item: MenuItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp), // separa icono y texto
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = item.color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3436),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


