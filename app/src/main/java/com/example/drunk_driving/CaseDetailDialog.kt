package com.example.drunk_driving

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.drunk_driving.model.CaseData
import com.example.drunk_driving.model.UserData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

enum class CaseDialogType {
    PUBLIC,
    POLICE
}

@Composable
fun CaseDetailDialog(
    case: CaseData?,
    type: CaseDialogType,
    onDismiss: () -> Unit
) {
    val reporterInfo = remember { mutableStateOf<UserData?>(null) }
    val showVideoPlayer = remember { mutableStateOf(false) }

    // 載入舉報人資訊
    LaunchedEffect(case?.reporterId) {
        if (type == CaseDialogType.POLICE) {
            case?.reporterId?.let { reporterId ->
                if (reporterId.isNotEmpty()) {
                    loadUserFromFirestore(reporterId) { userData ->
                        reporterInfo.value = userData
                    }
                }
            }
        }
    }

    // 影片播放對話框
    if (showVideoPlayer.value && case?.videoUrl?.isNotEmpty() == true) {
        VideoPlayerDialog(
            videoUrl = case.videoUrl,
            onDismiss = { showVideoPlayer.value = false }
        )
    }

    if (case != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "事件編號：${case.caseId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // 影像預覽
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray)
                            .clickable {
                                if (case.videoUrl.isNotEmpty()) {
                                    showVideoPlayer.value = true
                                }
                            }
                    ) {
                        if (case.videoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(case.videoUrl)
                                    .videoFrameMillis(1000) // 獲取第1秒的畫面作為縮圖
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "影片縮圖",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // 播放按鈕
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "播放影片",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(60.dp),
                                    tint = Color.White
                                )
                            }
                        } else {
                            // 沒有影片
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "無影片",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = "無影片資料",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 事件資訊
                    DetailInfoSection("經緯度位置:", "${case.location.geo.latitude}°${if(case.location.geo.latitude >= 0) "N" else "S"} ${case.location.geo.longitude}°${if(case.location.geo.longitude >= 0) "E" else "W"}")
                    DetailInfoSection("事件地點", case.location.address)
                    DetailInfoSection("發生時間", formatTimestamp(case.time))

                    if (type == CaseDialogType.POLICE) {
                        DetailInfoSection("事件分級", case.classification)

                        val reporter = reporterInfo.value
                        if (reporter != null) {
                            DetailInfoSection("舉發帳號:", reporter.email)
                            if (reporter.phone.isNotEmpty()) {
                                DetailInfoSection("聯絡電話:", reporter.phone)
                            }
                        } else {
                            DetailInfoSection("舉發帳號:", "無法取得資訊")
                            DetailInfoSection("聯絡電話:", "無法取得資訊")
                        }
                    }
                    else if (type == CaseDialogType.PUBLIC) {
                        DetailInfoSection("處理狀態:", case.status)
                        DetailInfoSection("類別", case.classification) // 要改
                    }
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

@Composable
fun VideoPlayerDialog(
    videoUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                // 關閉按鈕
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("關閉", color = Color.White)
                    }
                }

                // 影片播放器
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DetailInfoSection(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// 從 Firestore 載入用戶資料
private fun loadUserFromFirestore(userId: String, onResult: (UserData?) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("Users")
        .document(userId)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                try {
                    val userData = UserData(
                        email = document.getString("email") ?: "",
                        phone = document.getString("phone") ?: ""
                    )
                    onResult(userData)
                } catch (e: Exception) {
                    Log.e("Firestore", "Error converting user document: ${e.message}")
                    onResult(null)
                }
            } else {
                Log.d("Firestore", "No user document found for ID: $userId")
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user document: ", exception)
            onResult(null)
        }
}

// 格式化時間戳記
private fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}