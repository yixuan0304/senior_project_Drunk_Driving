package com.example.drunk_driving.video

import android.net.Uri
import com.example.drunk_driving.repository.CaseRepository
import com.example.drunk_driving.model.VideoData
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

object FirebaseStorageHelper {
    private val firebaseStorage = Firebase.storage.reference.child("videos")

    // 上傳影片並自動建立案件
    suspend fun uploadVideoAndCreateCase(
        uri: Uri,
        reporterId: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0,
        address: String = "待定位"
    ): Result<Pair<VideoData, String>> {
        return try {
            val videoData = uploadVideo(uri)

            val caseResult = CaseRepository.createCase(
                reporterId = reporterId,
                videoUrl = videoData.videoUrl,
                latitude = latitude,
                longitude = longitude,
                address = address
            )

            when {
                caseResult.isSuccess -> {
                    val caseId = caseResult.getOrThrow()
                    Result.success(Pair(videoData, caseId))
                }
                else -> {
                    Result.failure(caseResult.exceptionOrNull() ?: Exception("建立案件失敗"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 影片上傳
    suspend fun uploadVideo(uri: Uri): VideoData {
        val videoFileName = generateVideoFileName(uri)
        val videoReference = firebaseStorage.child(videoFileName)
        videoReference.putFile(uri).await()
        val url = videoReference.downloadUrl.await().toString()
        return VideoData(videoUrl = url, videoName = videoFileName, storagePath = videoReference.path)
    }

    // 取得所有影片
    suspend fun fetchAllVideos(): List<VideoData> {
        val result = firebaseStorage.listAll().await()
        return result.items.mapNotNull { ref ->
            try {
                val url = ref.downloadUrl.await().toString()
                val name = ref.name
                val path = ref.path
                VideoData(videoUrl = url, videoName = name, storagePath = path)
            } catch (e: Exception) {
                null // 忽略無法取得下載連結的檔案
            }
        }
    }

    // 生成影片檔名
    private fun generateVideoFileName(uri: Uri): String {
        val timestamp = System.currentTimeMillis()
        val originalName = uri.lastPathSegment?.split("/")?.last()
        return originalName?.let { "drunk_driving_${timestamp}_$it" }
            ?: "drunk_driving_video_$timestamp.mp4"
    }
}