package com.example.drunk_driving

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

@Composable
fun SelectIdentityPage(navController: NavController){
    var isPublicButtonClicked by remember { mutableStateOf(false) }
    var isPoliceButtonClicked by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            //返回按鈕
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                IconButton(onClick = {
                    navController.navigate("RegisterPage")
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "返回",
                        tint = White
                    )
                }
                Text(
                    text = "返回",
                    color = White,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("RegisterPage")
                        }
                        .padding(top = 10.dp)
                )
            }

            //logo
            Image(
                painter = painterResource(id = R.drawable.driving_logo),
                contentDescription = "logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
            )
        }

        //selectText
        Text(
            text = "請選擇你的身分",
            color = White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        //publicButton
        OutlinedButton(
            onClick = {
                isPublicButtonClicked = true
                isPoliceButtonClicked = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPublicButtonClicked) Color(0xFFE0E0FF) else White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .padding(vertical = 30.dp)
                .size(width = 300.dp, height = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.public_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .padding(end = 10.dp)

            )
            Text(
                text = "民眾",
                color = Color(0xF310108C),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //policeButton
        OutlinedButton(
            onClick = {
                isPoliceButtonClicked = true
                isPublicButtonClicked = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPoliceButtonClicked) Color(0xFFE0E0FF) else White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .size(width = 300.dp, height = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.police_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .padding(end = 10.dp)

            )
            Text(
                text = "警方",
                color = Color(0xF310108C),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //confirmButton
        OutlinedButton(
            onClick = {
                if (isPublicButtonClicked) {
                    navController.navigate("PublicHomePage")
                }
                else if (isPoliceButtonClicked) {
                    navController.navigate("PoliceIncidentManagementPage")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1d3c8a)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(vertical = 30.dp)
                .size(width = 180.dp, height = 65.dp)
        ) {
            Text(
                text = "確認",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectIdentityPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        SelectIdentityPage(navController = navController)
    }
}