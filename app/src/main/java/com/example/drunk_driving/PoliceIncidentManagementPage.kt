package com.example.drunk_driving

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceIncidentManagementPage(navController: NavController){
    val searchQuery = rememberSaveable() { mutableStateOf("") }
    var filteredCase = cases.filter { it.toString().contains(searchQuery.value, ignoreCase = true) }
    var selectedCase = rememberSaveable { mutableStateOf<CaseData?>(null) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5957b0),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(
                        "事件管理",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
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
                                navController.navigate("LoginPage")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.shutdown_icon),
                                contentDescription = null,
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
                Text(
                    text = "分級",
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
                                .clickable { selectedCase.value = case }
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
                            )
                            Column(
                                Modifier
                                    .fillMaxHeight(1f)
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    Modifier
                                        .size(16.dp)
                                        .background(getColorFormLevel(case.level), CircleShape)
                                )
                                Text(
                                    text = case.level,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                if(selectedCase.value != null){
                    CaseDetailDialog(
                        case = selectedCase,
                        onDismiss = {selectedCase.value = null}
                    )
                }
                //更新按鈕
                Button(
                    onClick = {
                        /*重新載入此頁*/
                    },
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

//搜尋功能
@Composable
fun SearchBar(searchQuery: MutableState<String>){
    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { newQuery: String -> searchQuery.value = newQuery },
        label = { Text("輸入關鍵字（編號、時間、地點）") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = ""
            )
        },
        modifier = Modifier
            .background(Color.White)
            .width(350.dp)
            .height(75.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

//點擊事件顯示事件詳情功能
@Composable
fun CaseDetailDialog(
    case: MutableState<CaseData?>,
    onDismiss: () -> Unit
) {
    // 確保 `case.value` 不為 null
    val currentCase = case.value
    if (currentCase != null){
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "事件編號：${currentCase.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    // 影像預覽 (模擬播放按鈕)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放影片",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 事件資訊
                    Text(
                        text = "經緯度位置：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentCase.latitude_longitude,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "事件地點：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentCase.location,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "發生時間：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentCase.time,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "事件分級：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentCase.level,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "舉發帳號：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentCase.report_account,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "聯絡電話：",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    var reportAccountPhone = members.filter { it.email == currentCase.report_account }.single().phone
                    Text(
                        text = reportAccountPhone,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("關閉")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PoliceIncidentManagementPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        PoliceIncidentManagementPage(navController = navController)
    }
}