package com.example.drunk_driving

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicHomePage(
    navController: NavController,
    onSignOut: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var addressText by remember { mutableStateOf("位置讀取中...") }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 權限請求launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // 檢查並請求權限
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                @SuppressLint("MissingPermission")
                val location = fusedClient.lastLocation.await()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLocation = latLng

                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(it.latitude, it.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                addressText = addresses[0].getAddressLine(0) ?: "找不到地址"
                            } else {
                                addressText = "找不到地址"
                            }
                        }
                    } else {
                        // 對於較舊的 Android 版本，使用舊方法
                        @Suppress("DEPRECATION")
                        val result = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (!result.isNullOrEmpty()) {
                            addressText = result[0].getAddressLine(0) ?: "找不到地址"
                        } else {
                            addressText = "找不到地址"
                        }
                    }
                } ?: run {
                    addressText = "無法取得位置"
                }
            } catch (e: Exception) {
                addressText = "位置讀取錯誤"
            }
        } else {
            addressText = "需要位置權限"
        }
    }

    val currentTime by produceState(initialValue = "") {
        while (true) {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            value = sdf.format(Date())
            delay(60 * 1000L) // 每分鐘更新
        }
    }

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
                                coroutineScope.launch {
                                    Firebase.auth.signOut()
                                    onSignOut()
                                    navController.navigate("LoginPage")
                                }
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
            MapScreen(currentLocation)

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
                Text(
                    currentTime,
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
                Text(
                    addressText,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun MapScreen(
    currentLocation: LatLng?
) {
    val cameraPositionState = remember {
        CameraPositionState()
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 16f)
        }
    }

    val uiSetting = MapUiSettings(
        zoomGesturesEnabled = true,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = true,
    )

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSetting,
    ) {
        currentLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "你的位置"
            )
        }
    }
}