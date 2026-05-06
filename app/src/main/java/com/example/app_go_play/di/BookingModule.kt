package com.example.app_go_play.di

import com.example.app_go_play.feature.booking.data.remote.BookingApi
import com.example.app_go_play.feature.booking.data.repository.BookingRepositoryImpl
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookingModule {

    @Provides
    @Singleton
    fun provideBookingRepository(api: BookingApi): BookingRepository {
        return BookingRepositoryImpl(api)
    }
}
