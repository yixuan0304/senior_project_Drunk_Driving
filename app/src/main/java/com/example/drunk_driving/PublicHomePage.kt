package com.example.drunk_driving

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicHomePage(navController: NavController){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5957b0),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(
                        "首頁",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF5957b0),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                /* 跳到PublicDrunkDrivingHistoryPage */
                                navController.navigate("PublicDrunkDrivingHistoryPage")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.folder_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                /* 跳到PublicHomePage */
                                navController.navigate("PublicHomePage")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Outlined.Home,
                                contentDescription = "Homepage",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(100.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                navController.navigate("LoginPage")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ExitToApp,
                                contentDescription = "Exit to app",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(100.dp)
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                onClick = { navController.navigate("CameraPhotoPage")},
                colors = ButtonDefaults.buttonColors(Color(0xFFd0d2e9)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp, end = 5.dp)
                    .size(width = 150.dp, height = 50.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text(
                        text = "錄影",
                        color = Black,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.record_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                            .padding(top = 5.dp)
                    )

                }
            }
            //串Map
            MapScreen()
            //
            Row(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp),
                horizontalArrangement = Arrangement.Start
            ){
                Image(
                    painter = painterResource(id = R.drawable.time_icon),
                    contentDescription = "Time",
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(25.dp)
                )
                //之後要根據真實時間改
                Text(
                    text = "2024/11/12 09:55",
                    fontSize = 20.sp
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp),
                horizontalArrangement = Arrangement.Start
            ){
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = "Location",
                    modifier = Modifier
                        .size(25.dp)
                )
                //之後要根據定位改
                Text(
                    text = "台北市信義區市府路1號",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun MapScreen(){
    Column(Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val nuk = LatLng(22.733184,120.281206)
        val cameraPosition = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(nuk,16f)
        )
        val uiSetting = MapUiSettings(
            zoomGesturesEnabled = true,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = true,
        )
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            cameraPositionState = cameraPosition,
            uiSettings = uiSetting,
        )
    }
}