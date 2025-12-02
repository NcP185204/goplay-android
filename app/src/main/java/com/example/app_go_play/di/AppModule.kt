package com.example.app_go_play.di

import com.example.app_go_play.feature.auth.data.remote.AuthApi
import com.example.app_go_play.feature.auth.data.repository.AuthRepositoryImpl
import com.example.app_go_play.feature.auth.domain.repository.AuthRepository
import com.example.app_go_play.feature.court.data.remote.CourtApi
import com.example.app_go_play.feature.court.data.repository.CourtRepositoryImpl
import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // SỬA LỖI: Đổi BASE_URL để trỏ đến server thật, không phải localhost của emulator
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // --- CẤU HÌNH "GIÁN ĐIỆP" LOGGING ---
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        // Tạo interceptor và set level là BODY để xem tất cả thông tin
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        // Xây dựng OkHttpClient và gắn interceptor vào
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    // -------------------------------------

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Sửa lại hàm này để nhận OkHttpClient đã được cấu hình
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gắn OkHttpClient vào Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- Auth Dependencies ---
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi): AuthRepository {
        return AuthRepositoryImpl(api)
    }

    // --- Court Dependencies ---
    @Provides
    @Singleton
    fun provideCourtApi(retrofit: Retrofit): CourtApi {
        return retrofit.create(CourtApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCourtRepository(api: CourtApi): CourtRepository {
        return CourtRepositoryImpl(api)
    }
}
