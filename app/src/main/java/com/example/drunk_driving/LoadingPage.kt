package com.example.drunk_driving

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun LoadingPage(
    navController: NavController,
    isUserSignedIn: Boolean
){
    LaunchedEffect(Unit) {
        delay(3000)
        if (isUserSignedIn) {
            navController.navigate("SelectIdentityPage") {
                popUpTo("LoadingPage") { inclusive = true }
            }
        } else {
            navController.navigate("LoginPage") {
                popUpTo("LoadingPage") { inclusive = true }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ){
        //logo
        Image(
            painter = painterResource(id = R.drawable.driving_logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(400.dp)
        )

        Text(
            text = "加載中...",
            color = White,
            fontSize = 30.sp
        )
    }
}