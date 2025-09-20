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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.drunk_driving.model.CaseData
import com.example.drunk_driving.model.Location
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicDrunkDrivingHistoryPage(navController: NavController){
    val searchQuery = rememberSaveable() { mutableStateOf("") }
    val cases = remember { mutableStateOf<List<CaseData>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val selectedCase = rememberSaveable() { mutableStateOf<CaseData?>(null) }

    // 排序相關狀態
    val sortField = remember { mutableStateOf(PublicSortField.TIME) }
    val sortAscending = remember { mutableStateOf(false) } // 預設最新的在前面


    // 載入當前用戶的案件資料
    val currentUser = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            loadUserCasesFromFirestore(userId) { caseList ->
                cases.value = caseList
                isLoading.value = false
            }
        }
    }

    // 根據排序狀態排序案件
    val sortedCases = remember(cases.value, sortField.value, sortAscending.value) {
        val sorted = when (sortField.value) {
            PublicSortField.CASE_ID -> {
                if (sortAscending.value) cases.value.sortedBy { it.caseId }
                else cases.value.sortedByDescending { it.caseId }
            }
            PublicSortField.TIME -> {
                if (sortAscending.value) cases.value.sortedBy { it.time.seconds }
                else cases.value.sortedByDescending { it.time.seconds }
            }
            PublicSortField.LOCATION -> {
                if (sortAscending.value) cases.value.sortedBy { it.location.address }
                else cases.value.sortedByDescending { it.location.address }
            }
            PublicSortField.STATUS -> {
                if (sortAscending.value) cases.value.sortedBy { it.status }
                else cases.value.sortedByDescending { it.status }
            }
        }
        sorted
    }

    val filteredCase = sortedCases.filter { case ->
        val searchText = searchQuery.value
        case.caseId.contains(searchText, ignoreCase = true) ||
                formatTimestamp(case.time).contains(searchText, ignoreCase = true) ||
                case.location.address.contains(searchText, ignoreCase = true) ||
                case.classification.contains(searchText, ignoreCase = true) ||
                case.status.contains(searchText, ignoreCase = true)
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 15.dp)
                    .background(Color(0xFF5957b0))
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ){
                // 可排序的案件編號標題
                PublicSortableHeaderCell(
                    text = "案件編號",
                    currentSortField = sortField.value,
                    targetField = PublicSortField.CASE_ID,
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

                // 可排序的時間標題
                PublicSortableHeaderCell(
                    text = "時間",
                    currentSortField = sortField.value,
                    targetField = PublicSortField.TIME,
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

                // 可排序的地點標題
                PublicSortableHeaderCell(
                    text = "地點",
                    currentSortField = sortField.value,
                    targetField = PublicSortField.LOCATION,
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

                // 可排序的狀態標題
                PublicSortableHeaderCell(
                    text = "狀態",
                    currentSortField = sortField.value,
                    targetField = PublicSortField.STATUS,
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
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        userScrollEnabled = true,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
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
                                Text(
                                    text = case.status,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                        }
                    }
                }

                if(selectedCase.value != null){
                    CaseDetailDialog(
                        case = selectedCase.value,
                        type = CaseDialogType.PUBLIC,
                        onDismiss = { selectedCase.value = null }
                    )
                }

                //更新按鈕
                Button(
                    onClick = {
                        currentUser?.uid?.let { userId ->
                            isLoading.value = true
                            loadUserCasesFromFirestore(userId) { caseList ->
                                cases.value = caseList
                                isLoading.value = false
                            }
                        } },
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

// 可排序的標題元件
@Composable
fun RowScope.PublicSortableHeaderCell(
    text: String,
    currentSortField: PublicSortField,
    targetField: PublicSortField,
    isAscending: Boolean,
    onSort: (PublicSortField) -> Unit
) {
    val isCurrentField = currentSortField == targetField

    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(
                if (isCurrentField) Color(0xFF4A47A3)
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
                fontSize = 12.sp
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


// 排序欄位枚舉
enum class PublicSortField {
    CASE_ID, TIME, LOCATION, STATUS
}

// 從 Firestore 載入當前用戶的案件資料
private fun loadUserCasesFromFirestore(userId: String, onResult: (List<CaseData>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("Cases")
        .whereEqualTo("reporterId", userId) // 只載入當前用戶的案件
        .orderBy("caseId", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val caseList = mutableListOf<CaseData>()
            for (document in documents) {
                try {
                    // 處理 location 物件
                    val locationMap = document.get("location") as? Map<*, *>
                    val location = if (locationMap != null) {
                        Location(
                            geo = locationMap["geo"] as? GeoPoint ?: GeoPoint(0.0, 0.0),
                            address = locationMap["address"] as? String ?: ""
                        )
                    } else {
                        Location()
                    }

                    val caseData = CaseData(
                        caseId = document.getString("caseId") ?: "",
                        reporterId = document.getString("reporterId") ?: "",
                        time = document.getTimestamp("time") ?: Timestamp.now(),
                        location = location,
                        classification = document.getString("classification") ?: "",
                        status = document.getString("status") ?: "",
                        videoUrl = document.getString("videoUrl") ?: "",
                        createdAt = document.getTimestamp("createdAt") ?: Timestamp.now()
                    )
                    caseList.add(caseData)
                } catch (e: Exception) {
                    Log.e("Firestore", "Error converting document: ${e.message}", e)
                }
            }
            onResult(caseList)
            Log.d("Firestore", "查詢 userId = $userId")

            for (document in documents) {
                Log.d("Firestore", "文件 ${document.id} 的 reporterId = ${document.getString("reporterId")}")
            }

        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user cases: ", exception)
            onResult(emptyList())
        }
}

// 格式化時間戳記
private fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

@Preview(showBackground = true)
@Composable
fun PublicDrunkDrivingHistoryPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        PublicDrunkDrivingHistoryPage(navController = navController)
    }
}
