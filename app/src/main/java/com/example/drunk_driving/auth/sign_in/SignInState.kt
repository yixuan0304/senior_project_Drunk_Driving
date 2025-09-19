package com.example.drunk_driving.auth.sign_in

import com.example.drunk_driving.model.UserData

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isGoogleSignIn: Boolean = false,
    val userIdentity: String? = null,
    val userData: UserData? = null
)
