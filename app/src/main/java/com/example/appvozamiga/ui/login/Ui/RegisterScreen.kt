package com.example.appvozamiga.ui.login.Ui

import android.net.http.HeaderBlock
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import  androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.appvozamiga.R

@Composable
fun RegisterScreen(){
    Box(modifier =
        Modifier
            .fillMaxSize()
            .padding(16.dp)){
        Register(Modifier.align(Alignment.Center))
    }
}

@Composable
fun Register(modifier: Modifier){
    Column (modifier = modifier){
        HeaderImage()
    }
}

@Composable
fun HeaderImage() {
    Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo")
}
