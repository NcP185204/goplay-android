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

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Thêm interceptor xác thực
            .addInterceptor(loggingInterceptor) // Thêm interceptor log
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
    fun provideAuthRepository(api: AuthApi, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
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
