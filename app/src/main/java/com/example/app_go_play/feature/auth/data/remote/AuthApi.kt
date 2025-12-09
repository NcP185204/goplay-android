package com.example.app_go_play.feature.auth.data.remote

import com.example.app_go_play.feature.auth.data.remote.dto.ForgotPasswordRequest
import com.example.app_go_play.feature.auth.data.remote.dto.LoginRequest
import com.example.app_go_play.feature.auth.data.remote.dto.LoginResponse
import com.example.app_go_play.feature.auth.data.remote.dto.LogoutRequest
import com.example.app_go_play.feature.auth.data.remote.dto.RegisterRequest
import com.example.app_go_play.feature.auth.data.remote.dto.SocialLoginRequest
import com.example.app_go_play.feature.auth.domain.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): User

    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest)

    @POST("api/auth/social-login")
    suspend fun socialLogin(@Body socialLoginRequest: SocialLoginRequest): LoginResponse
    
    // Sửa lại hàm logout để gửi refreshToken lên server
    @POST("api/auth/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest)


}
