package com.example.drunk_driving.auth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Email
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.R
import com.example.drunk_driving.members
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme

@Composable
fun ForgetPasswordPage(navController: NavController){
    var email by remember { mutableStateOf("") }
    var unknownAccount by remember { mutableStateOf<Boolean>(false) }
    var noEmail by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            //返回登入頁按鈕
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
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

        //emailText
        Text(
            text = "電子信箱",
            color = White,
            fontSize = 20.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .align(Alignment.CenterHorizontally)
        )

        //emailTextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("電子信箱") },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            trailingIcon = {
                Icon(
                    Icons.Rounded.Email,
                    contentDescription = "電子信箱圖示",
                    modifier = Modifier.size(24.dp)
                )
            }
        )

        //submitButton
        OutlinedButton(
            onClick = {
                /* 判斷email是否是已註冊帳號，若是，傳更改密碼的介面(?。若否，跳出尚未註冊的提示訊息 */
                noEmail = false
                unknownAccount = false
                var loginAccount = members.filter { it.email == email}
                if (email.isBlank()){
                    noEmail = true
                }else if (loginAccount.isEmpty()) {
                    unknownAccount = true
                }else {
                    navController.navigate("ResetPasswordPage")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
            border = BorderStroke(width = 2.dp, color = Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(top = 25.dp)
                .size(width = 150.dp, height = 50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "送出",
                color = Black,
                fontSize = 15.sp
            )
        }

        //noEmailWrongHint、unknownAccountWrongHint
        if (noEmail){
            Text(
                text = "請輸入電子信箱",
                color = Color(0xFFCA0000),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }else if (unknownAccount) {
            Text(
                text = "您尚未註冊帳號",
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

@Preview(showBackground = true)
@Composable
fun ForgetPasswordPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        ForgetPasswordPage(navController = navController)
    }
}