package com.example.app_go_play.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.domain.usecase.ForgotPasswordUseCase
import com.example.app_go_play.feature.auth.domain.usecase.LoginUseCase
import com.example.app_go_play.feature.auth.domain.usecase.RegisterUseCase
import com.example.app_go_play.feature.auth.domain.usecase.SocialLoginUseCase
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val socialLoginUseCase: SocialLoginUseCase // SỬA LỖI: Tiêm SocialLoginUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun register(fullName: String, email: String?, phoneNumber: String?, password: String, role: Role) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerUseCase(fullName, email, phoneNumber, password, role)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it.toString()) }, // Assuming User object, adjust if needed
                onFailure = { AuthState.Error(it.message ?: "An unexpected error occurred") }
            )
        }
    }

    fun login(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = loginUseCase(emailOrPhone, password)
            handleLoginResult(result, "Login failed")
        }
    }

    // SỬA LỖI: Triển khai lại hoàn toàn hàm này
    fun loginWithFacebook(accessToken: AccessToken) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Gọi đến use case chung với provider là "FACEBOOK"
            val result = socialLoginUseCase(accessToken.token, "FACEBOOK")
            handleLoginResult(result, "Facebook login failed")
        }
    }

    // SỬA LỖI: Triển khai lại hoàn toàn hàm này
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Gọi đến use case chung với provider là "GOOGLE"
            val result = socialLoginUseCase(idToken, "GOOGLE")
            handleLoginResult(result, "Google login failed")
        }
    }

    // Helper function to reduce code duplication
    private fun handleLoginResult(result: Result<String>, errorMessage: String) {
        _authState.value = result.fold(
            onSuccess = { token ->
                if (token.isNotBlank()) {
                    AuthState.Success(token)
                } else {
                    AuthState.Error("$errorMessage: No token was received from server.")
                }
            },
            onFailure = { AuthState.Error(it.message ?: errorMessage) }
        )
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = forgotPasswordUseCase(email)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success("Password reset link sent to your email.") },
                onFailure = { AuthState.Error(it.message ?: "An unexpected error occurred") }
            )
        }
    }

    fun setAuthLoading() {
        _authState.value = AuthState.Loading
    }

    fun setAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

// --- AuthState Sealed Class ---
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: String) : AuthState() // Holds JWT token or success message
    data class Error(val message: String) : AuthState()
}
