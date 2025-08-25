package com.example.drunk_driving.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.R
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

@Composable
fun ResetPasswordPage(navController: NavController) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmpasswordVisible by remember { mutableStateOf(false) }
    var noPasswordWrongHint by remember { mutableStateOf<Boolean?>(false) }
    var noConfirmPasswordWrongHint by remember { mutableStateOf<Boolean?>(false) }
    var validPassword by remember { mutableStateOf<Boolean?>(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //返回登入頁按鈕
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    /* 返回LoginPage */
                    navController.navigate("LoginPage")
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "返回登入頁",
                        tint = White
                    )
                }
                Text(
                    text = "返回登入頁",
                    color = White,
                    modifier = Modifier
                        // 點擊文字也能返回
                        .clickable {
                            /* 返回LoginPage */
                            navController.navigate("LoginPage")
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

        //passwordText
        Text(
            text = "密碼",
            color = White,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .align(Alignment.CenterHorizontally)
        )

        //passwordTextField
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("輸入新的密碼") },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) "🙈" else "👁️"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(icon)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        //confirmPasswordText
        Text(
            text = "確認密碼",
            color = White,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally)
        )

        //confirmPasswordTextField
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("再次輸入密碼") },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmpasswordVisible) "🙈" else "👁️"
                IconButton(onClick = { confirmpasswordVisible = !confirmpasswordVisible }) {
                    Text(icon)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        //updateButton
        OutlinedButton(
            onClick = {
                /* 判斷newPassword是否==confirmPassword，若是，把newPassword傳到帳號資料庫取代原本的密碼。若否，跳出confirmPassword != newPassword的提示訊息 */
                noPasswordWrongHint = false
                noConfirmPasswordWrongHint = false
                validPassword = true
                if (newPassword.isBlank()) {
                    noPasswordWrongHint = true
                } else if (confirmPassword.isBlank()) {
                    noConfirmPasswordWrongHint = true
                } else if (newPassword == confirmPassword) {
                    validPassword = true
                    navController.navigate("LoginPage")
                    /*把newPassword傳到帳號資料庫取代原本的密碼*/
                } else {
                    validPassword = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
            border = BorderStroke(width = 2.dp, color = Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .padding(top = 20.dp)
                .size(width = 150.dp, height = 50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "更新",
                color = Black,
                fontSize = 15.sp
            )
        }
        when {
            noPasswordWrongHint == true -> {
                Text(
                    text = "請輸入密碼",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            noConfirmPasswordWrongHint == true -> {
                Text(
                    text = "請輸入確認密碼",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            validPassword == false -> {
                Text(
                    text = "密碼與確認密碼不一致",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            noConfirmPasswordWrongHint == true -> {
                Text(
                    text = "請輸入確認密碼",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            validPassword == false -> {
                Text(
                    text = "密碼與確認密碼不一致",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        ResetPasswordPage(navController = navController)
    }
}