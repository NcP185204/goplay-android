package com.example.app_go_play.feature.auth.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthViewModel
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.PLAYER) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    val callbackManager = remember { CallbackManager.Factory.create() }
    val facebookLoginLauncher = rememberLauncherForActivityResult(
        contract = LoginManager.getInstance().createLogInActivityResultContract(callbackManager, null)
    ) {}

    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) { authViewModel.loginWithFacebook(result.accessToken) }
            override fun onCancel() { authViewModel.resetAuthState() }
            override fun onError(error: FacebookException) { authViewModel.setAuthError(error.message ?: "Facebook login failed") }
        })
    }

    Box(modifier = Modifier.fillMaxSize().background(SportBackground)) {
        // Top Header Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(listOf(SportPrimary, SportPrimary.copy(alpha = 0.8f))),
                    shape = RoundedCornerShape(bottomStart = 80.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Header Title
            Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = SportAccent, modifier = Modifier.size(48.dp))
            Text("TẠO TÀI KHOẢN", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)
            Text("Tham gia cộng đồng GoPlay ngay!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(30.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Họ và Tên") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SportPrimary) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SportPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SportPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SportPrimary) },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Xác nhận mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null, tint = SportPrimary) },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Role Selection
                    Text("Bạn đăng ký với vai trò:", fontWeight = FontWeight.Bold, color = SportPrimary, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = role == Role.PLAYER,
                            onClick = { role = Role.PLAYER },
                            colors = RadioButtonDefaults.colors(selectedColor = SportPrimary)
                        )
                        Text("Người chơi", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        RadioButton(
                            selected = role == Role.OWNER,
                            onClick = { role = Role.OWNER },
                            colors = RadioButtonDefaults.colors(selectedColor = SportPrimary)
                        )
                        Text("Chủ sân", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register Button
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = SportPrimary)
                    } else {
                        Button(
                            onClick = {
                                when {
                                    password != confirmPassword -> errorMsg = "Mật khẩu không khớp."
                                    fullName.isBlank() -> errorMsg = "Vui lòng nhập họ tên."
                                    email.isBlank() && phoneNumber.isBlank() -> errorMsg = "Vui lòng nhập Email hoặc SĐT."
                                    else -> {
                                        errorMsg = null
                                        authViewModel.register(fullName, email.ifBlank { null }, phoneNumber.ifBlank { null }, password, role)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportPrimary)
                        ) {
                            Text("ĐĂNG KÝ NGAY", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            // Error Display
            val currentError = errorMsg ?: (if (authState is AuthState.Error) (authState as AuthState.Error).message else null)
            currentError?.let {
                Text(it, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Register
            Text("Hoặc đăng ký nhanh bằng", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                onClick = { 
                    authViewModel.setAuthLoading()
                    facebookLoginLauncher.launch(listOf("email", "public_profile")) 
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                color = Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Facebook, contentDescription = null, tint = Color(0xFF4267B2))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Đăng ký với Facebook", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Row(modifier = Modifier.padding(bottom = 40.dp)) {
                Text("Đã có tài khoản? ", color = Color.Gray)
                Text(
                    "Đăng nhập", 
                    color = SportPrimary, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }
    }
}
