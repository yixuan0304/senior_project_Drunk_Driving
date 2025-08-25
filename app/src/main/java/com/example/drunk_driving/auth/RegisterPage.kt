package com.example.drunk_driving.auth

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
            )
        }

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
                            Text("感謝您使用本應用程式（以下稱為「本APP」）。在使用本APP前，請您仔細閱讀並同意以下條款（以下稱為「本條款」）。若您不同意本條款的任何部分，請勿使用本APP。\n" +
                                    "\n" +
                                    "一、使用條件\n" +
                                    "\n" +
                                    "適用範圍：本條款適用於所有使用本APP的用戶（以下稱為「用戶」）。\n" +
                                    "\n" +
                                    "法定年齡：您必須年滿18歲，或在監護人同意下使用本APP。\n" +
                                    "\n" +
                                    "帳戶註冊：部分功能需要用戶註冊帳戶，註冊時您必須提供準確且最新的個人資訊，並妥善保管您的帳戶資料。\n" +
                                    "\n" +
                                    "二、服務內容\n" +
                                    "\n" +
                                    "服務功能：本APP提供的具體服務功能以應用程式內說明為準。\n" +
                                    "\n" +
                                    "更新與修改：本APP有權隨時更新、修改或中止全部或部分服務，恕不另行通知。\n" +
                                    "\n" +
                                    "第三方服務：本APP可能整合或連結第三方服務，但不對其內容或行為負責。\n" +
                                    "\n" +
                                    "三、用戶責任\n" +
                                    "\n" +
                                    "合法使用：用戶承諾遵守所有適用法律及本條款的規定，不得利用本APP從事任何非法或不當行為，包括但不限於：\n" +
                                    "\n" +
                                    "傳播違法、侵權或不當內容；\n" +
                                    "\n" +
                                    "未經授權訪問其他用戶的資料；\n" +
                                    "\n" +
                                    "干擾或破壞本APP的正常運行。\n" +
                                    "\n" +
                                    "資料安全：用戶應妥善保護其帳戶及密碼，對其帳戶下的所有行為負責。\n" +
                                    "\n" +
                                    "四、隱私保護\n" +
                                    "\n" +
                                    "個人資料收集與使用：本APP將依據《隱私政策》處理用戶的個人資料。請參閱我們的《隱私政策》以了解詳情。\n" +
                                    "\n" +
                                    "資料安全：我們將採取合理的技術措施保護用戶資料，但無法保證完全免於風險。\n" +
                                    "\n" +
                                    "五、智慧財產權\n" +
                                    "\n" +
                                    "所有權：本APP中的所有內容（包括但不限於程式碼、設計、文字、圖像等）均屬於本APP或其授權方所有，受相關法律保護。\n" +
                                    "\n" +
                                    "限制使用：未經書面許可，用戶不得修改、分發、複製或商業利用本APP內容。\n" +
                                    "\n" +
                                    "六、免責聲明\n" +
                                    "\n" +
                                    "服務穩定性：本APP將努力確保服務的可用性和穩定性，但對因不可抗力或其他非本APP可控因素導致的服務中斷、不準確或損失不承擔責任。\n" +
                                    "\n" +
                                    "用戶責任：因用戶違反本條款或相關法律規定而產生的任何損失或糾紛，本APP不承擔責任。\n" +
                                    "\n" +
                                    "七、條款修改\n" +
                                    "\n" +
                                    "修改權利：本APP有權隨時修改本條款，並在APP內發布更新版本。更新後的條款自發布時生效。\n" +
                                    "\n" +
                                    "持續使用：若用戶在條款修改後繼續使用本APP，即視為接受修訂內容。\n" +
                                    "\n" +
                                    "八、準據法與管轄\n" +
                                    "\n" +
                                    "準據法：本條款受所在地法律管轄。\n" +
                                    "\n" +
                                    "爭議解決：若發生任何爭議，雙方應首先以友好協商方式解決，若協商不成，應提交至所在地具有管轄權的法院處理。\n" +
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
                            Text("非常歡迎您下載本App，為了讓您能夠安心使用本App的各項服務與資訊，特此向您說明本App的隱私權保護政策，以保障您的權益，請您詳閱下列內容：\n" +
                                    "\n" +
                                    "一、隱私權保護政策的適用範圍\n" +
                                    "隱私權保護政策內容，包括本App如何處理在您使用App服務時收集到的個人識別資料。隱私權保護政策不適用於本App以外的相關連結外部網頁，也不適用於非本App所委託或參與管理的人員。\n" +
                                    "\n" +
                                    "二、個人資料的蒐集、處理及利用方式\n" +
                                    "本App將向使用者蒐集以下個人資料。若您拒絕提供，將無法提供您使用本 APP 相關服務：\n" +
                                    "\n" +
                                    " ● 當您登入使用本App時，根據本App提供服務性質不同，我們會請您提供個人資訊，包括：電子信箱、密碼、電話及其他相關必要資料。\n" +
                                    " ● 於一般連線時，伺服器會自行記錄相關行徑，包括您使用連線設備的IP位址、使用時間、使用的作業系統、瀏覽及點選資料記錄等，做為我們增進App服務的參考依據，此記錄為內部應用，決不對外公佈。\n" +
                                    "\n" +
                                    "三、資料之保護\n" +
                                    " ● 本App主機均設有防火牆、防毒系統等相關的各項資訊安全設備及必要的安全防護措施，加以保護網站及您的個人資料採用嚴格的保護措施，只由經過授權的人員才能接觸您的個人資料，相關處理人員皆簽有保密合約，如有違反保密義務者，將會受到相關的法律處分。\n" +
                                    " ● 如因業務需要有必要委託其他單位提供服務時，本App亦會嚴格要求其遵守保密義務，並且採取必要檢查程序以確定其將確實遵守。\n" +
                                    "\n" +
                                    "四、網站對外的相關連結\n" +
                                    "本App的網頁提供其他網站的網路連結，您也可經由本App所提供的連結，點選進入其他網站。但該連結網站不適用本App的隱私權保護政策，您必須參考該連結網站中的隱私權保護政策。\n" +
                                    "\n" +
                                    "五、與第三人共用個人資料之政策\n" +
                                    "本App絕不會提供、交換、出租或出售任何您的個人資料給其他個人、團體、私人企業或公務機關，但有法律依據或合約義務者，不在此限。 前項但書之情形包括不限於：\n" +
                                    "\n" +
                                    " ● 經由您書面同意。\n" +
                                    " ● 法律明文規定。\n" +
                                    " ● 為免除您生命、身體、自由或財產上之危險。\n" +
                                    " ● 與公務機關或學術研究機構合作，基於公共利益為統計或學術研究而有必要，且資料經過提供者處理或蒐集著依其揭露方式無從識別特定之當事人。\n" +
                                    " ● 當您在App裡的行為，違反服務條款或可能損害或妨礙App與其他使用者權益或導致任何人遭受損害時，經App管理單位研析揭露您的個人資料是為了辨識、聯絡或採取法律行動所必要者。\n" +
                                    " ● 有利於您的權益。\n" +
                                    "\n" +
                                    "本App委託廠商協助蒐集、處理或利用您的個人資料時，將對委外廠商或個人善盡監督管理之責。\n" +
                                    "\n" +
                                    "六、隱私權保護政策之修正\n" +
                                    "本App隱私權保護政策將因應需求隨時進行修正。",fontSize = 12.sp)
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


        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    isRegisterButtonClicked = true
                    if (email.isNotBlank() && isValidEmail(email) &&
                        password.isNotBlank() &&
                        confirmPassword.isNotBlank() && password == confirmPassword &&
                        phoneNumber.isNotBlank() && isValidPhoneNumber(phoneNumber) &&
                        read_selectedOption
                    ) {
                        navController.navigate("SelectIdentityPage")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7BADC)),
                border = BorderStroke(width = 2.dp, color = Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(top = 25.dp)
                    .size(width = 180.dp, height = 50.dp)
            ) {
                Text(
                    text = "註冊",
                    color = Black,
                    fontSize = 15.sp
                )
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