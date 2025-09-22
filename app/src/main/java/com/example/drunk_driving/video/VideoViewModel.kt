package com.example.drunk_driving.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunk_driving.model.VideoData
import kotlinx.coroutines.launch

class VideoViewModel: ViewModel() {
    var videoList by mutableStateOf<List<VideoData>>(emptyList())
        private set

    init {
        fetchVideos()
    }

    fun addVideo(video: VideoData) {
        videoList = videoList + video
    }

    fun fetchVideos() {
        viewModelScope.launch {
            videoList = FirebaseStorageHelper.fetchAllVideos()
        }
    }
}