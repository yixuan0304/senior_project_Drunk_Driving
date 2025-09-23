package com.example.drunk_driving

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.auth.isValidPhoneNumber
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun SelectIdentityPage(
    navController: NavController,
    email: String = "",
    phoneNumber: String = "",
    isGoogleLogin: Boolean = false
){
    var isPublicButtonClicked by remember { mutableStateOf(false) }
    var isPoliceButtonClicked by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isConfirmButtonClicked by remember { mutableStateOf(false) }
    var inputPhoneNumber by remember { mutableStateOf(phoneNumber) }
    var phoneError by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            //返回按鈕
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                IconButton(onClick = {
                    if (isGoogleLogin) {
                        // Google 登入用戶應該返回到登入頁面重新選擇
                        navController.navigate("LoginPage") {
                            popUpTo("LoginPage") { inclusive = true }
                        }
                    } else {
                        // 一般註冊用戶返回註冊頁面
                        navController.navigate("RegisterPage")
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "返回",
                        tint = White
                    )
                }
                Text(
                    text = "返回",
                    color = White,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("RegisterPage")
                        }
                        .padding(top = 10.dp)
                )
            }

            //logo
            Image(
                painter = painterResource(id = R.drawable.driving_logo),
                contentDescription = "logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
            )
        }

        // Google 登入用戶的電話號碼輸入欄位
        if (isGoogleLogin) {
            Text(
                text = "請輸入您的手機號碼",
                color = White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            TextField(
                value = inputPhoneNumber,
                onValueChange = { newValue ->
                    inputPhoneNumber = newValue
                    phoneError = if (newValue.isNotEmpty() && !isValidPhoneNumber(newValue)) {
                        "請輸入正確的手機號碼格式 (09xxxxxxxx)"
                    } else {
                        ""
                    }
                },
                label = { Text("輸入手機號碼") },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(300.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError.isNotEmpty()
            )

            if (phoneError.isNotEmpty()) {
                Text(
                    text = phoneError,
                    color = Color(0xFFCA0000),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                )
            }
        }

        //selectText
        Text(
            text = "請選擇您的身分",
            color = White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        //publicButton
        OutlinedButton(
            onClick = {
                isPublicButtonClicked = true
                isPoliceButtonClicked = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPublicButtonClicked) Color(0xFFE0E0FF) else White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(width = 300.dp, height = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.public_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .padding(end = 10.dp)

            )
            Text(
                text = "民眾",
                color = Color(0xF310108C),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //policeButton
        OutlinedButton(
            onClick = {
                isPoliceButtonClicked = true
                isPublicButtonClicked = false
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPoliceButtonClicked) Color(0xFFE0E0FF) else White
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .size(width = 300.dp, height = 80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.police_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .padding(end = 10.dp)

            )
            Text(
                text = "警方",
                color = Color(0xF310108C),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //confirmButton
        OutlinedButton(
            onClick = {
                isConfirmButtonClicked = true

                // 驗證身分選擇
                if (!isPublicButtonClicked && !isPoliceButtonClicked) {
                    return@OutlinedButton
                }

                // 如果是 Google 登入，驗證電話號碼
                if (isGoogleLogin) {
                    if (inputPhoneNumber.isEmpty()) {
                        phoneError = "請輸入電話號碼"
                        return@OutlinedButton
                    }
                    if (!isValidPhoneNumber(inputPhoneNumber)) {
                        phoneError = "請輸入正確的手機號碼格式 (09xxxxxxxx)"
                        return@OutlinedButton
                    }
                    phoneError = ""
                }

                val currentUser = Firebase.auth.currentUser
                if (currentUser != null) {
                    isLoading = true

                    // 準備用戶資料
                    val userIdentity = if (isPublicButtonClicked) "public" else "police"
                    val finalPhoneNumber = if (isGoogleLogin) inputPhoneNumber else phoneNumber

                    val userData = hashMapOf(
                        "uId" to currentUser.uid,
                        "email" to email,
                        "phone" to finalPhoneNumber,
                        "identity" to userIdentity,
                        "loginMethod" to if (isGoogleLogin) "google" else "email",
                        "createdAt" to Timestamp.now()
                    )

                    // 將用戶資料存入 Firestore
                    Firebase.firestore.collection("Users")
                        .document(currentUser.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "註冊完成！", Toast.LENGTH_SHORT).show()

                            // 根據身分導向不同頁面
                            if (isPublicButtonClicked) {
                                navController.navigate("PublicHomePage") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else if (isPoliceButtonClicked) {
                                navController.navigate("PoliceIncidentManagementPage") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false
                            Toast.makeText(
                                context,
                                "註冊失敗: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    Toast.makeText(context, "用戶驗證失敗，請重新註冊", Toast.LENGTH_SHORT).show()
                    navController.navigate("RegisterPage")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1d3c8a)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(vertical = 30.dp)
                .size(width = 180.dp, height = 65.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "確認",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // 錯誤訊息顯示
        if (isConfirmButtonClicked && (!isPublicButtonClicked && !isPoliceButtonClicked)) {
            Text(
                text = "請選擇身分",
                color = Color(0xFFCA0000),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectIdentityPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        SelectIdentityPage(
            navController = navController,
            email = "test@gmail.com",
            isGoogleLogin = true
        )
    }
}