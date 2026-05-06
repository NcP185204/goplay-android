package com.example.app_go_play.feature.auth.presentation.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.navigation.NavController
import com.example.app_go_play.R
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthViewModel
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthState
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

// Palette màu đồng bộ với HomeScreen
val SportPrimary = Color(0xFF1A237E)
val SportAccent = Color(0xFFC6FF00)
val SportBackground = Color(0xFFF4F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val credentialManager = remember { CredentialManager.create(context) }
    val callbackManager = remember { CallbackManager.Factory.create() }
    val facebookLoginLauncher = rememberLauncherForActivityResult(
        contract = LoginManager.getInstance().createLogInActivityResultContract(callbackManager, null),
        onResult = { /* Callback handles it */ }
    )

    LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            authViewModel.loginWithFacebook(result.accessToken)
        }
        override fun onCancel() { authViewModel.resetAuthState() }
        override fun onError(error: FacebookException) {
            authViewModel.setAuthError(error.message ?: "Facebook login failed")
        }
    })

    Box(modifier = Modifier.fillMaxSize().background(SportBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(listOf(SportPrimary, SportPrimary.copy(alpha = 0.8f))),
                    shape = RoundedCornerShape(bottomStart = 80.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            Icon(
                Icons.Default.SportsSoccer, 
                contentDescription = null, 
                tint = SportAccent, 
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "GO PLAY",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )
            Text(
                text = "Sẵn sàng ra sân!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(60.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Đăng nhập", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = SportPrimary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("Email hoặc Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SportPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SportPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SportPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Quên mật khẩu?", 
                        modifier = Modifier.align(Alignment.End).clickable { },
                        color = SportPrimary, 
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = SportPrimary)
                    } else {
                        Button(
                            onClick = { authViewModel.login(emailOrPhone, password) },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportPrimary)
                        ) {
                            Text("ĐĂNG NHẬP", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Hoặc tiếp tục với", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIcon(label = "Google") {
                    authViewModel.setAuthLoading()
                    coroutineScope.launch {
                        try {
                            val googleIdTokenOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(context.getString(R.string.google_web_client_id))
                                .build()
                            val request = GetCredentialRequest.Builder().addCredentialOption(googleIdTokenOption).build()
                            val result = credentialManager.getCredential(context, request)
                            val credential = result.credential
                            when (credential) {
                                is GoogleIdTokenCredential -> authViewModel.loginWithGoogle(credential.idToken)
                                is CustomCredential -> {
                                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        authViewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                    }
                                }
                                is PasswordCredential -> authViewModel.login(credential.id, credential.password)
                            }
                        } catch (e: Exception) {
                            authViewModel.setAuthError("Google sign-in failed: ${e.message}")
                        }
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                SocialIcon(label = "Facebook") {
                    authViewModel.setAuthLoading()
                    facebookLoginLauncher.launch(listOf("email", "public_profile"))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text("Chưa có tài khoản? ", color = Color.Gray)
                Text(
                    "Đăng ký ngay", 
                    color = SportPrimary, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("register") }
                )
            }
        }
    }
}

@Composable
fun SocialIcon(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (label == "Google") {
                // ĐÃ CẬP NHẬT: Sử dụng file ảnh ic_google.webp của bạn
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Unspecified // Giữ nguyên màu của logo gốc
                )
            } else {
                Icon(
                    Icons.Default.Facebook, 
                    contentDescription = label, 
                    tint = Color(0xFF4267B2),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
