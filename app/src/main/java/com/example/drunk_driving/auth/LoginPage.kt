package com.example.drunk_driving.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.drunk_driving.R
import com.example.drunk_driving.auth.sign_in.SignInState

@Composable
fun LoginPage(
    navController: NavController,
    state: SignInState,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(state.signInError) {
        state.signInError?.let { error ->
            showError = true // 顯示錯誤訊息
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // 登入成功時隱藏錯誤訊息
    LaunchedEffect(state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            showError = false
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ) {
        //logo
        Image(
            painter = painterResource(id = R.drawable.driving_logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(250.dp)
        )

        //emailTextField
        TextField(
            value = email,
            onValueChange = {
                email = it
                // 使用者重新輸入時，隱藏錯誤訊息
                if (showError) showError = false
            },
            label = { Text("電子信箱") },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 5.dp),
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

        //passwordTextField
        TextField(
            value = password,
            onValueChange = { password = it
                // 使用者重新輸入時，隱藏錯誤訊息
                if (showError) showError = false
            },
            label = { Text("密碼") },
            modifier = Modifier
                .padding(top = 25.dp)
                .width(350.dp)
                .padding(horizontal = 5.dp),
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

        Row(
            modifier = Modifier.width(350.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //emailPasswordWrongHint
            if(showError){
                Text(
                    text = "電子信箱或密碼錯誤",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f)
                )
            } else {
                Text("", modifier = Modifier.weight(1f))
            }

            //forgetPasswordButton
            TextButton(
                onClick = {
                    navController.navigate("ForgetPasswordPage")
                }
            ) {
                Text(
                    text = "忘記密碼？",
                    color = White,
                    fontSize = 15.sp
                )
            }
        }

        // loginButton
        OutlinedButton(
            onClick = { onEmailSignInClick(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
            border = BorderStroke(2.dp, Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(vertical = 25.dp).size(width = 180.dp, height = 50.dp)
        ) {
            Text("登入", color = Black, fontSize = 15.sp)
        }

        // Google loginButton
        OutlinedButton(
            onClick = onGoogleSignInClick,
            colors = ButtonDefaults.buttonColors(containerColor = White),
            border = BorderStroke(2.dp, Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.size(width = 180.dp, height = 50.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp).padding(end = 5.dp)
                )
                Text("Google登入", color = Black, fontSize = 15.sp, modifier = Modifier.align(Alignment.CenterVertically))
            }
        }

        //registerButton
        OutlinedButton(
            onClick = {
                /* 跳到RegisterPage */
                navController.navigate("RegisterPage")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
            border = BorderStroke(width = 2.dp, color = Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(vertical = 25.dp)
                .size(width = 180.dp, height = 50.dp)
        ) {
            Text(
                text = "註冊新帳號",
                color = Black,
                fontSize = 15.sp
            )
        }
    }
}