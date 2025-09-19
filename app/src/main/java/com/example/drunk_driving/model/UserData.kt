package com.example.drunk_driving.model

data class UserData (
    val email: String,
    val createdAt: String = "",
    val identity: String = "",
    val loginMethod: String = "",
    val phone: String = "",
    val uId: String = ""
)
