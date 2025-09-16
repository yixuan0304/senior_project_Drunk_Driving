package com.example.drunk_driving.video

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

object FirebaseStorageHelper {
    private val firebaseStorage = Firebase.storage.reference.child("videos")

    // 上傳 video 至 Firebase Storage
    suspend fun uploadVideo(uri: Uri): VideoData {
        val videoFileName = uri.lastPathSegment?.split("/")?.last() ?: "video_${System.currentTimeMillis()}"
        val videoReference = firebaseStorage.child(videoFileName)
        videoReference.putFile(uri).await()
        val url = videoReference.downloadUrl.await().toString()
        return VideoData(videoUrl = url, videoName = videoFileName, storagePath = videoReference.path)
    }
}