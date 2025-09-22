package com.example.drunk_driving.utils

import android.content.Context
import android.util.Log
import com.example.drunk_driving.repository.CaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppInitializer {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_FIRST_LAUNCH = "is_first_launch"

    // 應用程式首次啟動時的初始化
    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

        if (isFirstLaunch) {
            CoroutineScope(Dispatchers.IO).launch {
                initializeApp()

                // 標記為已初始化
                prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            }
        }
    }

    // 執行初始化任務
    private suspend fun initializeApp() {
        try {
            // 初始化案件計數器（從 1 開始，這樣第一個案件會是 C00001）
            val result = CaseRepository.initializeCaseCounter(startFrom = 0)

            result.onSuccess {
                Log.d("AppInitializer", "案件計數器初始化成功")
            }.onFailure { e ->
                Log.e("AppInitializer", "案件計數器初始化失敗", e)
            }

        } catch (e: Exception) {
            Log.e("AppInitializer", "應用程式初始化失敗", e)
        }
    }
}