package com.example.drunk_driving.model

import com.google.firebase.Timestamp

data class UserData (
    val email: String,
    val createdAt: Timestamp = Timestamp.now(),
    val identity: String = "",
    val loginMethod: String = "",
    val phone: String = "",
    val uId: String = ""
)
