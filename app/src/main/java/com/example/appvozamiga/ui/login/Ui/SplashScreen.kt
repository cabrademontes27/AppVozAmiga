package com.example.appvozamiga.ui.login.Ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appvozamiga.R
import androidx.compose.runtime.getValue



@Composable
fun SplashScreen() {
    val colors = listOf(
        Color(0xFFA4937F), // Brown
        Color(0xFFE4EEFF), // Light blue
        Color(0xFFFFFFFF)  // White
    )

    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = colors[0],
        targetValue = colors[2],
        animationSpec = infiniteRepeatable(
            animation = tween(2000), // 2 seconds per color
            repeatMode = RepeatMode.Reverse // Ping-pong effect
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedColor), // Animated background
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}