package com.example.drunk_driving.auth.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                isGoogleSignIn = true,
                userData = result.data
            )
        }
    }

    fun resetState() {
        _state.update {
            SignInState()
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()

                if (result.user != null) {
                    // 登入成功後，查詢用戶的 identity
                    val userIdentity = getUserIdentityFromDatabase(result.user!!.uid)

                    _state.update {
                        it.copy(
                            isSignInSuccessful = true,
                            signInError = null,
                            isGoogleSignIn = false,
                            userIdentity = userIdentity,  // 設置用戶身份
                            userData = UserData(
                                userId = result.user!!.uid,
                                username = result.user!!.displayName ?: "",
                                email = result.user!!.email ?: email
                            )
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = "登入失敗",
                            isGoogleSignIn = false,
                            userIdentity = null,
                            userData = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSignInSuccessful = false,
                        signInError = e.message,
                        isGoogleSignIn = false,
                        userIdentity = null,
                        userData = null
                    )
                }
            }
        }
    }
}

private suspend fun getUserIdentityFromDatabase(uid: String): String? {
    return try {
        val userDoc = Firebase.firestore
            .collection("Users")
            .document(uid)
            .get()
            .await()

        userDoc.getString("identity")
    } catch (e: Exception) {
        null
    }
}