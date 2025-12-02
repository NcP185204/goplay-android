package com.example.app_go_play.di

import com.example.app_go_play.feature.auth.domain.repository.AuthRepository
import com.example.app_go_play.feature.auth.domain.usecase.ForgotPasswordUseCase
import com.example.app_go_play.feature.auth.domain.usecase.LoginUseCase
import com.example.app_go_play.feature.auth.domain.usecase.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    // SỬA LỖI: Đã xóa hàm provideAuthRepository() bị trùng lặp.
    // Hilt sẽ tự động sử dụng hàm provideAuthRepository từ AppModule.

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideForgotPasswordUseCase(authRepository: AuthRepository): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(authRepository)
    }
}
