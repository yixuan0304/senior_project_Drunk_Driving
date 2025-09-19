package com.example.drunk_driving.auth.sign_in

import com.example.drunk_driving.model.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)