package com.example.drunk_driving

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.example.drunk_driving.model.CaseData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceIncidentManagementPage(
    navController: NavController,
    onSignOut: () -> Unit
){
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val cases = remember { mutableStateOf<List<CaseData>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val selectedCase = rememberSaveable { mutableStateOf<CaseData?>(null) }

    // 排序相關狀態
    val sortField = remember { mutableStateOf(SortField.CASE_ID) }
    val sortAscending = remember { mutableStateOf(true) }

    // 載入案件資料
    LaunchedEffect(Unit) {
        loadCasesFromFirestore { caseList ->
            cases.value = caseList
            isLoading.value = false
        }
    }

    // 根據排序狀態排序案件
    val sortedCases = remember(cases.value, sortField.value, sortAscending.value) {
        val sorted = when (sortField.value) {
            SortField.CASE_ID -> {
                if (sortAscending.value) cases.value.sortedBy { it.caseId }
                else cases.value.sortedByDescending { it.caseId }
            }
            SortField.TIME -> {
                if (sortAscending.value) cases.value.sortedBy { it.time.seconds }
                else cases.value.sortedByDescending { it.time.seconds }
            }
            SortField.LOCATION -> {
                if (sortAscending.value) cases.value.sortedBy { it.location.address }
                else cases.value.sortedByDescending { it.location.address }
            }
            SortField.CLASSIFICATION -> {
                val priorityOrder = mapOf("疑似酒駕" to 1, "高風險" to 2, "中高風險" to 3, "非酒駕" to 4)
                if (sortAscending.value) {
                    cases.value.sortedBy { priorityOrder[it.classification] ?: 5 }
                } else {
                    cases.value.sortedByDescending { priorityOrder[it.classification] ?: 0 }
                }
            }
        }
        sorted
    }

    val filteredCase = sortedCases.filter { case ->
        val searchText = searchQuery.value
        case.caseId.contains(searchText, ignoreCase = true) ||
                formatTimestamp(case.time).contains(searchText, ignoreCase = true) ||
                case.location.address.contains(searchText, ignoreCase = true) ||
                case.classification.contains(searchText, ignoreCase = true)
    }

    val coroutineScope = rememberCoroutineScope()

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
                                coroutineScope.launch {
                                    Firebase.auth.signOut()
                                    onSignOut()
                                    navController.navigate("LoginPage") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
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
                // 可點擊的案件編號標題
                SortableHeaderCell(
                    text = "案件編號",
                    currentSortField = sortField.value,
                    targetField = SortField.CASE_ID,
                    isAscending = sortAscending.value,
                    onSort = { field ->
                        if (sortField.value == field) {
                            sortAscending.value = !sortAscending.value
                        } else {
                            sortField.value = field
                            sortAscending.value = true
                        }
                    }
                )

                // 可點擊的時間標題
                SortableHeaderCell(
                    text = "時間",
                    currentSortField = sortField.value,
                    targetField = SortField.TIME,
                    isAscending = sortAscending.value,
                    onSort = { field ->
                        if (sortField.value == field) {
                            sortAscending.value = !sortAscending.value
                        } else {
                            sortField.value = field
                            sortAscending.value = true
                        }
                    }
                )

                // 可點擊的地點標題
                SortableHeaderCell(
                    text = "地點",
                    currentSortField = sortField.value,
                    targetField = SortField.LOCATION,
                    isAscending = sortAscending.value,
                    onSort = { field ->
                        if (sortField.value == field) {
                            sortAscending.value = !sortAscending.value
                        } else {
                            sortField.value = field
                            sortAscending.value = true
                        }
                    }
                )

                // 可點擊的分級標題
                SortableHeaderCell(
                    text = "AI判斷",
                    currentSortField = sortField.value,
                    targetField = SortField.CLASSIFICATION,
                    isAscending = sortAscending.value,
                    onSort = { field ->
                        if (sortField.value == field) {
                            sortAscending.value = !sortAscending.value
                        } else {
                            sortField.value = field
                            sortAscending.value = true
                        }
                    }
                )
            }

            Box {
                if (isLoading.value) {
                    // 載入中的顯示
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (filteredCase.isEmpty() && searchQuery.value.isNotBlank()) {
                    // 有搜尋但無結果
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "找不到符合的結果",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "搜尋關鍵字：${searchQuery.value}",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "請嘗試其他關鍵字",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                } else if (cases.value.isEmpty()) {
                    // 完全沒有案件資料
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "目前沒有任何案件",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    // 顯示案件列表
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
                            ) {
                                Text(
                                    text = case.caseId,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = formatTimestamp(case.time),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Text(
                                    text = case.location.address,
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
                                            .background(getColorFormLevel(case.classification), CircleShape)
                                    )
                                    Text(
                                        text = case.classification,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                if(selectedCase.value != null){
                    CaseDetailDialog(
                        case = selectedCase.value,
                        type = CaseDialogType.POLICE,
                        onDismiss = { selectedCase.value = null }
                    )
                }

                //更新按鈕
                Button(
                    onClick = {
                        isLoading.value = true
                        loadCasesFromFirestore { caseList ->
                            cases.value = caseList
                            isLoading.value = false
                        }
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

// 搜尋功能
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

// 排序欄位枚舉
enum class SortField {
    CASE_ID, TIME, LOCATION, CLASSIFICATION
}

// 可排序的標題元件
@Composable
fun RowScope.SortableHeaderCell(
    text: String,
    currentSortField: SortField,
    targetField: SortField,
    isAscending: Boolean,
    onSort: (SortField) -> Unit
) {
    val isCurrentField = currentSortField == targetField

    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(
                if (isCurrentField) Color(0xFF4A47A3) // 表示當前排序欄位
                else Color(0xFF5957b0)
            )
            .clickable { onSort(targetField) },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            if (isCurrentField) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isAscending) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (isAscending) "升序排列" else "降序排列",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 從 Firestore 載入案件資料
private fun loadCasesFromFirestore(onResult: (List<CaseData>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("Cases")
        .orderBy("caseId", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val caseList = mutableListOf<CaseData>()
            for (document in documents) {
                try {
                    val caseData = document.toObject(CaseData::class.java)
                    caseList.add(caseData)
                } catch (e: Exception) {
                    // 處理資料轉換錯誤
                    Log.e("Firestore", "Error converting document: ${e.message}")
                }
            }
            onResult(caseList)
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
            onResult(emptyList())
        }
}

// 格式化時間戳記
private fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

// 根據分級取得顏色
private fun getColorFormLevel (level: String): Color {
    return when(level){
        "高風險" -> Color(0xFFFF0000)
        "中風險" -> Color(0xFFFFA500)
        else -> Color(0xFF00FF00)
    }
}