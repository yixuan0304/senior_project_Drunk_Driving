package com.example.drunk_driving.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppInitializer {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_FIRST_LAUNCH = "is_first_launch"

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

        if (isFirstLaunch) {
            Log.d("AppInitializer", "偵測到首次啟動，開始初始化...")

            CoroutineScope(Dispatchers.IO).launch {
                initializeApp()
                prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
                Log.d("AppInitializer", "首次啟動標記已設定")
            }
        } else {
            Log.d("AppInitializer", "非首次啟動，跳過初始化")
        }
    }

    private fun initializeApp() {
        try {
            // 計數器會在首次建立案件時自動初始化
            Log.d("AppInitializer", "應用程式初始化完成")
        } catch (e: Exception) {
            Log.e("AppInitializer", "應用程式初始化失敗", e)
        }
    }
}