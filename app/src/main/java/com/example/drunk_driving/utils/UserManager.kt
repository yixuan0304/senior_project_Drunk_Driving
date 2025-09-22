package com.example.drunk_driving.utils

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

object UserManager {
    // 取得目前登入使用者的ID
    fun getCurrentUserId(): String {
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("User not signed in")
    }
}