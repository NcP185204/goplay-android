package com.example.app_go_play.feature.auth.presentation.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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

@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- GOOGLE SIGN-IN ---
    val credentialManager = remember { CredentialManager.create(context) }

    // --- FACEBOOK SIGN-IN ---
    val callbackManager = remember { CallbackManager.Factory.create() }
    val facebookLoginLauncher = rememberLauncherForActivityResult(
        contract = LoginManager.getInstance().createLogInActivityResultContract(callbackManager, null),
        onResult = { /* Callback handles it */ }
    )

    LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            Log.d("LoginScreen_Facebook", "onSuccess: Token received")
            authViewModel.loginWithFacebook(result.accessToken)
        }

        override fun onCancel() {
            Log.w("LoginScreen_Facebook", "onCancel: Login canceled by user.")
            authViewModel.resetAuthState()
        }

        override fun onError(error: FacebookException) {
            Log.e("LoginScreen_Facebook", "onError: ${error.message}", error)
            authViewModel.setAuthError(error.message ?: "Facebook login failed")
        }
    })


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome !", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { authViewModel.login(emailOrPhone, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("Or sign in with", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
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

                        // SỬA LỖI: Xử lý các loại thông tin đăng nhập khác nhau, bao gồm cả CustomCredential
                        when (credential) {
                            is GoogleIdTokenCredential -> {
                                // Trường hợp 1 (Legacy): Trực tiếp nhận GoogleIdTokenCredential
                                authViewModel.loginWithGoogle(credential.idToken)
                            }
                            is CustomCredential -> {
                                // Trường hợp 2 (Hiện đại): Nhận một CustomCredential và kiểm tra xem có phải của Google không
                                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    try {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        authViewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                    } catch (e: GoogleIdTokenParsingException) {
                                        Log.e("LoginScreen", "Failed to parse Google ID token from CustomCredential", e)
                                        authViewModel.setAuthError("Failed to parse Google ID token.")
                                    }
                                } else {
                                    Log.w("LoginScreen", "Received an unsupported CustomCredential type: ${credential.type}")
                                    authViewModel.setAuthError("Unsupported custom credential type received.")
                                }
                            }
                            is PasswordCredential -> {
                                // Trường hợp 3: Người dùng chọn mật khẩu đã lưu (Google Smart Lock)
                                val email = credential.id
                                val pwd = credential.password
                                emailOrPhone = email
                                password = pwd
                                authViewModel.login(email, pwd)
                            }
                            else -> {
                                // Trường hợp khác: Loại không xác định
                                Log.w("LoginScreen", "Received an unexpected credential type: ${credential::class.java.name}")
                                authViewModel.setAuthError("Unsupported credential type received.")
                            }
                        }

                    } catch (e: GetCredentialException) {
                        when (e) {
                            is NoCredentialException -> {
                                Log.d("LoginScreen", "No Google credentials available on the device.")
                                authViewModel.setAuthError("No Google accounts found. Please add a Google account to your device settings.")
                            }
                            else -> {
                                Log.e("LoginScreen", "Google One Tap GetCredentialException", e)
                                authViewModel.setAuthError("Google sign-in failed: ${e.message}")
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In with Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                authViewModel.setAuthLoading()
                facebookLoginLauncher.launch(listOf("email", "public_profile"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In with Facebook")
        }

        if (authState is AuthState.Error) {
            val errorState = authState as AuthState.Error
            Text(
                text = errorState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Don\'t have an account? Register",
            modifier = Modifier.clickable { navController.navigate("register") }
        )
    }
}
