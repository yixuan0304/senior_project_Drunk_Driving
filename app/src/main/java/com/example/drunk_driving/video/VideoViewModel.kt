package com.example.drunk_driving.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VideoViewModel: ViewModel() {
    var videoList by mutableStateOf<List<VideoData>>(emptyList())
        private set

    fun addVideo(video: VideoData) {
        videoList = videoList + video
    }
}