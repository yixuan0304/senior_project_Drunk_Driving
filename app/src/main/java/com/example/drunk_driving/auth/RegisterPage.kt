package com.example.drunk_driving.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.drunk_driving.R
import com.example.drunk_driving.ui.theme.Drunk_DrivingTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun RegisterPage(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var read_selectedOption by remember { mutableStateOf(false) }
    var showTermsOfServiceDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var isRegisterButtonClicked by remember { mutableStateOf(false) }
    var verificationEmailSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) } //檢查驗證流程
    val context = LocalContext.current

    // 檢查Email驗證狀態
    LaunchedEffect(verificationEmailSent) {
        if (verificationEmailSent) {
            isChecking = true
            while (isChecking && verificationEmailSent) {
                Firebase.auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                    if (reloadTask.isSuccessful && Firebase.auth.currentUser?.isEmailVerified == true) {
                        isChecking = false
                        Toast.makeText(context, "Email驗證成功！請選擇您的身分", Toast.LENGTH_LONG).show()
                        // 傳遞註冊資料
                        navController.navigate("SelectIdentityPage/$email/$phoneNumber") {
                            popUpTo("RegisterPage") { inclusive = true }
                        }
                    }
                }
                delay(3000)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7178B3))
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            //返回登入頁按鈕
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                IconButton(onClick = {
                    /* 返回LoginPage */
                    verificationEmailSent = false
                    isChecking = false
                    navController.navigate("LoginPage") {
                        popUpTo("LoginPage") { inclusive = true }
                    }
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
                            verificationEmailSent = false
                            isChecking = false
                            navController.navigate("LoginPage") {
                                popUpTo("LoginPage") { inclusive = true }
                            }
                        }
                        .padding(top = 10.dp)
                )
            }

            //logo
            Image(
                painter = painterResource(id = R.drawable.driving_logo),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
            )
        }

        // 如果已經寄送驗證信，顯示驗證提示
        if (verificationEmailSent) {
            Text(
                text = "驗證信已寄到\n$email\n請前往信箱點擊驗證連結",
                textAlign = TextAlign.Center,
                color = White,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )

            if (isChecking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "正在檢查驗證狀態...",
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            //email
            InputLabelWithError(
                label = "電子信箱",
                showError = isRegisterButtonClicked && (email.isBlank() || !isValidEmail(email)),
                errorMessage = if (email.isBlank()) "請輸入電子信箱" else "電子信箱格式錯誤"
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("輸入電子信箱") },
                modifier = Modifier.width(350.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                trailingIcon = {
                    Icon(Icons.Rounded.Email, contentDescription = "電子信箱圖示", modifier = Modifier.size(24.dp))
                }
            )

            //password
            InputLabelWithError(
                label = "密碼（６個字元以上）",
                showError = isRegisterButtonClicked && password.isBlank(),
                errorMessage = "請輸入密碼"
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("輸入密碼") },
                modifier = Modifier.width(350.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) "🙈" else "👁️"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) { Text(icon) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            //確認密碼
            InputLabelWithError(
                label = "確認密碼",
                showError = isRegisterButtonClicked &&
                        (confirmPassword.isBlank() || password != confirmPassword),
                errorMessage = if (confirmPassword.isBlank()) "請確認密碼" else "密碼與確認密碼不一致"
            )
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("再次輸入密碼") },
                modifier = Modifier.width(350.dp),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) "🙈" else "👁️"
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Text(icon) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            //手機號碼
            InputLabelWithError(
                label = "手機號碼",
                showError = isRegisterButtonClicked && (phoneNumber.isBlank() || !isValidPhoneNumber(phoneNumber)),
                errorMessage = if (phoneNumber.isBlank()) "請輸入手機號碼" else "手機號碼格式錯誤"
            )
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("輸入手機號碼") },
                modifier = Modifier.width(350.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            //服務條款、隱私權保護政策
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = read_selectedOption,
                    onClick = { read_selectedOption = !read_selectedOption }
                )
                Text(text = "我已閱讀並同意 ", color = White)
                Text(
                    text = "服務條款",
                    color = Color.Yellow,
                    modifier = Modifier.clickable { showTermsOfServiceDialog = true }
                )
                Text(" 及 ", color = White)
                Text(
                    text = "隱私權保護政策",
                    color = Color.Yellow,
                    modifier = Modifier.clickable { showPrivacyPolicyDialog = true }
                )
            }

            if (isRegisterButtonClicked && !read_selectedOption) {
                Text(
                    text = "請閱讀並同意服務條款及隱私權保護政策",
                    color = Color(0xFFCA0000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 服務條款 Dialog
        if (showTermsOfServiceDialog) {
            AlertDialog(
                onDismissRequest = { showTermsOfServiceDialog = false },
                title = { Text("服務條款") },
                text = {
                    Box(modifier = Modifier.height(300.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text("感謝您使用「全民監督酒駕系統」（以下簡稱本系統），以下為本系統的使用者服務條款，為保障您的權益，請詳閱下列內容。若您點選「同意使用者服務條款」，則視為您已閱讀、了解並接受本系統之服務規範。\n" +
                                    "\n" +
                                    "一、服務內容\n" +
                                    "本系統旨在提升道路安全，民眾端之使用者可透過手機APP錄影，並由機器學習模型即時分析路上機車是否存在疑似酒駕之行為。若系統判定為疑似酒駕事件，將自動生成案件資料，協助民眾檢舉，並送交至警方端供其追蹤。\n" +
                                    "\n" +
                                    "二、使用者規範\n" +
                                    "1.使用者須具備完全行為能力。 \n" +
                                    "2.使用者必須遵守法律規範，且不得將本系統用於非法或惡意之行為。\n" +
                                    "3.使用者使用本系統時不得提供不實資訊、影響系統運作或其他違反公共秩序之行為。\n" +
                                    "4.如違反上述規範，本系統有權停止服務並聯繫執法單位進行處理。\n" +
                                    "\n" +
                                    "三、智慧財產權\n" +
                                    "本系統之軟體程式、設計、商標及模型均屬開發團隊所有，未經同意不得擅自使用或修改。非經開發團隊同意，使用者不得擅自複製、修改、散布或作商業用途。\n" +
                                    "\n" +
                                    "四、免責聲明\n" +
                                    "本系統之判斷結果僅供警方參考，最終裁量權仍由執法單位決定。\n" +
                                    "\n" +
                                    "五、服務調整與中止\n" +
                                    "開發團隊得視需求隨時修改、更新或停止部分服務內容。如有重大變更，將於系統介面公告或以其他方式通知使用者。\n" +
                                    "\n" +
                                    "六、適用法律與管轄\n" +
                                    "本條款受中華民國法律管轄。\n" +
                                    "\n" +
                                    "感謝您閱讀並遵守本服務條款！\n" +
                                    "\n",fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTermsOfServiceDialog = false }) {
                        Text("關閉")
                    }
                }
            )
        }

        // 隱私權保護政策 Dialog
        if (showPrivacyPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyPolicyDialog = false },
                title = { Text("隱私權保護政策") },
                text = {
                    Box(modifier = Modifier.height(300.dp)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text("感謝您使用「全民監督酒駕系統」（以下簡稱本系統），以下為本系統的隱私權保護政策，為保障您的權益，請詳閱下列內容。若您點選「同意隱私權政策」，則視為您已閱讀、了解並接受本系統之隱私權規範。\n" +
                                    "\n" +
                                    "一、個人資料之蒐集與處理\n" +
                                    "1.為提供個人化服務，在您初次使用本系統時，系統會提示您進行會員註冊。本系統之使用者類別包含「民眾」及「警方」，且本系統為此兩類使用者提供的服務並不相同，請依據您所屬的身份，確實選擇使用者類別。\n" +
                                    "\n" +
                                    "2.本系統在您註冊會員時，會保留您所提供的電子郵件地址、手機電話、密碼等資訊，以利您後續能順利登入本系統。此外，本系統的自動檢舉功能在回報疑似酒駕案件時，會一併帶入您的個人資料，供警方檢視，因此請確保您所提供的個人資料正確無誤。\n" +
                                    "\n" +
                                    "3.對於民眾端使用者，本系統在偵測到疑似酒駕案件時，會擷取您所拍攝到的路況影片、GPS位置、時間及個人資料，並彙整成案件資料至警方端。\n" +
                                    "\n" +
                                    "4.針對上述之個人資料，本系統不會將其用於酒駕偵測及檢舉以外的用途，以此保障您的個人資料隱私。\n" +
                                    "\n" +
                                    "二、個人資料之保護\n" +
                                    "本系統採取嚴謹的安全措施管理資料庫，保護系統及使用者個人資料的安全性，避免遭未經授權者竊取機密資訊。此外，本系統限制僅有極少數且擁有授權的資料庫管理人員能存取使用者資料，降低資料外洩的風險，讓使用者能夠安心使用本系統服務。\n" +
                                    "\n" +
                                    "三、隱私權保護政策之修正\n" +
                                    "本系統的隱私權保護政策將因應需求隨時進行修正，修正後的條款將在更新後提示使用者。\n" +
                                    "\n" +
                                    "四、聯繫管道\n" +
                                    "若您對於本系統之隱私權政策有任何疑問，或者想提出變更、移除個人資料之請求，歡迎聯繫本系統之開發團隊進行處理。\n" +
                                    "\n",fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                        Text("確定")
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    if (!verificationEmailSent) {
                        // 註冊流程
                        isRegisterButtonClicked = true
                        if (email.isNotBlank() && isValidEmail(email) &&
                            password.isNotBlank() &&
                            confirmPassword.isNotBlank() && password == confirmPassword &&
                            phoneNumber.isNotBlank() && isValidPhoneNumber(phoneNumber) &&
                            read_selectedOption
                        ) {
                            isLoading = true
                            Firebase.auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Firebase.auth.currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener { verificationTask ->
                                                if (verificationTask.isSuccessful) {
                                                    Toast.makeText(context, "驗證信已寄出，請至信箱收取", Toast.LENGTH_LONG).show()
                                                    verificationEmailSent = true
                                                } else {
                                                    Toast.makeText(context, "寄送驗證信失敗: ${verificationTask.exception?.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(context, task.exception?.message ?: "註冊失敗", Toast.LENGTH_SHORT).show()
                                        verificationEmailSent = false
                                    }
                                }
                        }
                    } else {
                        // 重新寄送驗證信
                        Firebase.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "驗證信已重新寄出", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "寄送失敗: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
                border = BorderStroke(width = 2.dp, color = Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(top = 25.dp)
                    .size(width = 180.dp, height = 50.dp),
                enabled = !isLoading // 載入中時禁用按鈕
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (verificationEmailSent) "重新寄送驗證信" else "註冊",
                        color = Black,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InputLabelWithError(label: String, showError: Boolean, errorMessage: String) {
    Row(modifier = Modifier.width(350.dp), horizontalArrangement = Arrangement.Start) {
        Text(text = label, color = White, fontSize = 20.sp, textAlign = TextAlign.Start)
        if (showError) {
            Text(
                text = errorMessage,
                color = Color(0xFFCA0000),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

//EmailRegex
fun isValidEmail(email: String):Boolean{
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return emailRegex.matches(email)
}

//PhoneRegex
fun isValidPhoneNumber(phoneNumber: String):Boolean{
    val phoneNumberRegex = Regex("^09\\d{8}$")
    return phoneNumberRegex.matches(phoneNumber)
}

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview(){
    Drunk_DrivingTheme {
        val navController = rememberNavController()
        RegisterPage(navController = navController)
    }
}