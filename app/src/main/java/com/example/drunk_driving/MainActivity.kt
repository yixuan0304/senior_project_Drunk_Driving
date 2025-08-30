package com.example.drunk_driving

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drunk_driving.auth.ForgetPasswordPage
import com.example.drunk_driving.auth.LoginPage
import com.example.drunk_driving.auth.RegisterPage
import com.example.drunk_driving.auth.ResetPasswordPage
import com.example.drunk_driving.auth.sign_in.GoogleAuthUiClient
import com.example.drunk_driving.auth.sign_in.SignInViewModel
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Drunk_DrivingTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.statusBars // 讓 Scaffold 考慮狀態列
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // 把 Scaffold 算好的安全區 padding 套到 Box -> 讓 NavHost 包含的畫面都不會被狀態列擋到
                    ) {
                        val navController = rememberNavController()
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if(result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if(state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                // 檢查是否為 Google 登入
                                if(state.isGoogleSignIn) {
                                    val userEmail = state.userData?.email ?: ""
                                    navController.navigate("SelectIdentityPage/$userEmail/true")
                                } else {
                                    when(state.userIdentity) {
                                        "police" -> {
                                            navController.navigate("PoliceIncidentManagementPage")
                                        }
                                        "public" -> {
                                            navController.navigate("PublicHomePage")
                                        }
                                        // 如果 identity 為空或無效，跳轉到身份選擇頁面
                                        else -> {
                                            navController.navigate("SelectIdentityPage")
                                        }
                                    }
                                }
                                viewModel.resetState()
                            }
                        }

                        NavHost(navController = navController, startDestination = "LoadingPage") {
                            composable("LoadingPage") {
                                LoadingPage(
                                    navController,
                                    isUserSignedIn = googleAuthUiClient.getSignedInUser() != null
                                )
                            }
                            composable("LoginPage") {
                                LoginPage(
                                    navController = navController,
                                    state = state,
                                    onGoogleSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                                            )
                                        }
                                    },
                                    onEmailSignInClick = { email, password ->
                                        lifecycleScope.launch {
                                            viewModel.signInWithEmail(email, password)
                                        }
                                    }
                                )
                            }
                            composable("ForgetPasswordPage") {
                                ForgetPasswordPage(navController)
                            }
                            composable("ResetPasswordPage") {
                                ResetPasswordPage(navController)
                            }
                            composable("RegisterPage") {
                                RegisterPage(navController)
                            }
                            composable("PoliceIncidentManagementPage") {
                                PoliceIncidentManagementPage(
                                    navController,
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                            composable("PublicHomePage") {
                                PublicHomePage(
                                    navController,
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Sign out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                            composable("PublicDrunkDrivingHistoryPage") {
                                PublicDrunkDrivingHistoryPage(navController)
                            }
                            composable("SelectIdentityPage") {
                                SelectIdentityPage(navController)
                            }
                            // 一般註冊用戶
                            composable(
                                "SelectIdentityPage/{email}/{phoneNumber}",
                                arguments = listOf(
                                    navArgument("email") { type = NavType.StringType },
                                    navArgument("phoneNumber") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                SelectIdentityPage(
                                    navController,
                                    email = backStackEntry.arguments?.getString("email") ?: "",
                                    phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: "",
                                    isGoogleLogin = false
                                )
                            }
                            //Google 登入用戶
                            composable(
                                "SelectIdentityPage/{email}/{isGoogleLogin}",
                                arguments = listOf(
                                    navArgument("email") { type = NavType.StringType },
                                    navArgument("isGoogleLogin") { type = NavType.BoolType }
                                )
                            ) { backStackEntry ->
                                SelectIdentityPage(
                                    navController,
                                    email = backStackEntry.arguments?.getString("email") ?: "",
                                    phoneNumber = "",
                                    isGoogleLogin = backStackEntry.arguments?.getBoolean("isGoogleLogin") ?: false
                                )
                            }
                            composable("CameraPhotoPage") {
                                CameraPhotoPage(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}