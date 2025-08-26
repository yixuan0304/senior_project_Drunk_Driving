package com.example.drunk_driving

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.auth.ForgetPasswordPage
import com.example.drunk_driving.auth.LoginPage
import com.example.drunk_driving.auth.RegisterPage
import com.example.drunk_driving.auth.ResetPasswordPage
import com.example.drunk_driving.auth.WaitForVerificationPage
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

class MainActivity : ComponentActivity() {
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
                        NavHost(navController = navController, startDestination = "LoadingPage") {
                            composable("LoadingPage") {
                                LoadingPage(navController)
                            }
                            composable("LoginPage") {
                                LoginPage(navController)
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
                                PoliceIncidentManagementPage(navController)
                            }
                            composable("PublicHomePage") {
                                PublicHomePage(navController)
                            }
                            composable("PublicDrunkDrivingHistoryPage") {
                                PublicDrunkDrivingHistoryPage(navController)
                            }
                            composable("SelectIdentityPage") {
                                SelectIdentityPage(navController)
                            }
                            composable("CameraPhotoPage") {
                                CameraPhotoPage(navController)
                            }
                            composable("WaitForVerificationPage") {
                                WaitForVerificationPage(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}