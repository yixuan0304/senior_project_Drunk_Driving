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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicDrunkDrivingHistoryPage(navController: NavController){
    val searchQuery = rememberSaveable() { mutableStateOf("") }
    val filteredCase = cases.filter { it.toString().contains(searchQuery.value, ignoreCase = true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5957b0),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(
                        "檢舉記錄",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
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
                                /* 登出，跳到LoginPage */
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //搜尋框
            SearchBar(searchQuery)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 15.dp)
            ){
                Text(
                    text = "案件編號",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(Color(0xFF5957b0))
                        .padding(top = 5.dp)
                )
                Text(
                    text = "時間",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(Color(0xFF5957b0))
                        .padding(top = 5.dp)
                )
                Text(
                    text = "地點",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(Color(0xFF5957b0))
                        .padding(top = 5.dp)
                )
            }

            Box {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    userScrollEnabled = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFd0d2e9))
                ) {
                    items(items = filteredCase) { case ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .heightIn(min = 60.dp)
                                .clickable{}
                        ){
                            Text(
                                text = case.id,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Text(
                                text = case.time,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Text(
                                text = case.location,
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp)
                            )
                        }
                    }
                }
                //更新按鈕
                Button(
                    onClick = {/*重新載入此頁*/},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .size(width = 100.dp, height = 50.dp)
                ){
                    Text(
                        "更新",
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicDrunkDrivingHistoryPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        PublicDrunkDrivingHistoryPage(navController = navController)
    }
}
