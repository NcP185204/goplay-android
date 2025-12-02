package com.example.app_go_play.di

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import com.example.app_go_play.feature.home.domain.usecase.GetTopCourtsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object HomeModule {

    @Provides
    @ViewModelScoped
    fun provideGetTopCourtsUseCase(courtRepository: CourtRepository): GetTopCourtsUseCase {
        return GetTopCourtsUseCase(courtRepository)
    }
}
