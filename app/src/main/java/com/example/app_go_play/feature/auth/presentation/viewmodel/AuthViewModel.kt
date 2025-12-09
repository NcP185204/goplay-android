package com.example.app_go_play.feature.auth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.di.TokenManager
import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.domain.usecase.ForgotPasswordUseCase
import com.example.app_go_play.feature.auth.domain.usecase.LoginUseCase
import com.example.app_go_play.feature.auth.domain.usecase.LogoutUseCase
import com.example.app_go_play.feature.auth.domain.usecase.RegisterUseCase
import com.example.app_go_play.feature.auth.domain.usecase.SocialLoginUseCase
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val socialLoginUseCase: SocialLoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            delay(500) 
            if (tokenManager.accessToken != null) {
                Log.d("AuthViewModel", "Access Token found. User is logged in.")
                _authState.value = AuthState.Success("Logged in successfully")
            } else {
                Log.d("AuthViewModel", "No Access Token found. User is not logged in.")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun register(fullName: String, email: String?, phoneNumber: String?, password: String, role: Role) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = registerUseCase(fullName, email, phoneNumber, password, role)
            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it.toString()) },
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

    fun loginWithFacebook(accessToken: AccessToken) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = socialLoginUseCase(accessToken.token, "FACEBOOK")
            handleLoginResult(result, "Facebook login failed")
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = socialLoginUseCase(idToken, "GOOGLE")
            handleLoginResult(result, "Google login failed")
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error logging out from server, proceeding with client-side cleanup", e)
            } finally {
                LoginManager.getInstance().logOut()
                _authState.value = AuthState.Unauthenticated
                Log.d("AuthViewModel", "User logged out locally. Tokens cleared.")
            }
        }
    }

    private fun handleLoginResult(result: Result<String>, errorMessage: String) {
        _authState.value = result.fold(
            onSuccess = { 
                AuthState.Success(it)
            },
            onFailure = { 
                AuthState.Error(it.message ?: errorMessage) 
            }
        )
    }

    fun setAuthLoading() {
        _authState.value = AuthState.Loading
    }

    fun setAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun resetAuthState() {
        if (_authState.value !is AuthState.Success) {
             _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Unknown : AuthState()
    object Unauthenticated : AuthState()
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
