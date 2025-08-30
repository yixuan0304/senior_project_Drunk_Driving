package com.example.drunk_driving.auth.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String,
    val email: String
)
