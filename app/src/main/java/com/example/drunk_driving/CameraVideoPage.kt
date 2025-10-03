package com.example.drunk_driving

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunk_driving.utils.LocationHelper
import com.example.drunk_driving.utils.UserManager
import com.example.drunk_driving.video.FirebaseStorageHelper
import com.example.drunk_driving.video.VideoViewModel
import kotlinx.coroutines.launch

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPhotoPage(
    navController: NavController,
    videoViewModel: VideoViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    // 權限請求
    LaunchedEffect(Unit) {
        if (!hasRequiredPermissions(context)) {
            activity?.let {
                ActivityCompat.requestPermissions(it, REQUIRED_PERMISSIONS, 0)
            }
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState()

    var isRecording by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

    // 預覽組件
    val previewView = remember { PreviewView(context) }

    // 初始化相機，綁定 Preview 和 VideoCapture
    LaunchedEffect(key1 = Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        val videoCaptureTemp = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                videoCaptureTemp
            )
            videoCapture = videoCaptureTemp
        } catch (exc: Exception) {
            Log.e("Camera", "Use case binding failed", exc)
        }
    }

    // 位置資訊狀態
    var locationInfo by remember { mutableStateOf<LocationHelper.LocationInfo?>(null) }

    LaunchedEffect(Unit) {
        locationInfo = LocationHelper.getCurrentLocationInfo(context)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {}
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Camera Preview
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            //返回按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                IconButton(
                    onClick = {
                        navController.navigate("PublicHomePage") {
                            popUpTo("PublicHomePage") { inclusive = true }
                        }
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
                        // 點擊文字也能返回
                        .clickable {
                            navController.navigate("PublicHomePage") {
                                popUpTo("PublicHomePage") { inclusive = true }
                            }
                        }
                        .padding(top = 10.dp)
                )
            }

            // 上傳進度
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = White
                    )
                }
                Text(
                    text = "正在上傳影片並建立案件...",
                    color = White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 80.dp)
                )
            }

            // 錄影開始 / 停止按鈕
            IconButton(
                onClick = {
                    if (videoCapture == null) return@IconButton

                    if (isRecording) {
                        recording?.stop()
                        recording = null
                        isRecording = false
                    } else {
                        if (!hasRequiredPermissions(context)) {
                            Toast.makeText(context, "缺少權限", Toast.LENGTH_SHORT).show()
                            return@IconButton
                        }
                        try {
                            val name = "video_${System.currentTimeMillis()}.mp4"
                            val file = File(context.filesDir, name)
                            val outputOptions = FileOutputOptions.Builder(file).build()

                            recording = videoCapture!!.output.prepareRecording(context, outputOptions)
                                .withAudioEnabled()
                                .start(ContextCompat.getMainExecutor(context)) { event ->
                                    if (event is VideoRecordEvent.Finalize) {
                                        if (event.hasError()) {
                                            Log.e(
                                                "CameraVideo",
                                                "錄影失敗: ${event.error}, ${event.cause}, ${event.outputResults.outputUri}"
                                            )
                                            Toast.makeText(context, "錄影失敗", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // 錄影成功，開始上傳到 Firebase
                                            isUploading = true

                                            coroutineScope.launch {
                                                try {
                                                    val videoUri = Uri.fromFile(file)

                                                    // 同時上傳影片和建立案件
                                                    val result = FirebaseStorageHelper.uploadVideoAndCreateCase(
                                                        uri = videoUri,
                                                        reporterId = UserManager.getCurrentUserId(),
                                                        latitude = locationInfo?.latitude ?: 0.0,
                                                        longitude = locationInfo?.longitude ?: 0.0,
                                                        address = locationInfo?.address ?: "位置未知"
                                                    )

                                                    result.onSuccess { (videoData, caseId) ->
                                                        // 更新ViewModel
                                                        videoViewModel.addVideo(videoData)

                                                        // 刪除本地檔案
                                                        file.delete()

                                                        isUploading = false
                                                        Toast.makeText(
                                                            context,
                                                            "影片上傳成功！案件編號: $caseId",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }.onFailure { e ->
                                                        Log.e("CameraVideo", "上傳或建立案件失敗", e)
                                                        isUploading = false
                                                        Toast.makeText(
                                                            context,
                                                            "處理失敗: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("CameraVideo", "處理過程發生錯誤", e)
                                                    isUploading = false
                                                    Toast.makeText(context, "處理失敗: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                        recording = null
                                        isRecording = false
                                    }
                                }
                            isRecording = true
                        } catch (e: SecurityException) {
                            Log.e("CameraVideo", "錄影權限不足", e)
                            Toast.makeText(context, "錄影權限不足", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(100.dp),
                enabled = !isUploading // 上傳時禁用按鈕
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.RadioButtonUnchecked else Icons.Default.RadioButtonChecked,
                    contentDescription = if (isRecording) "停止錄影" else "開始錄影",
                    tint = if (isRecording) White else Color.Red,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

private fun hasRequiredPermissions(context: Context): Boolean =
    REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
