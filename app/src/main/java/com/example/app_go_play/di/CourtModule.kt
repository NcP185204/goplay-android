package com.example.app_go_play.di

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import com.example.app_go_play.feature.court.domain.usecase.GetAvailableTimeSlotsUseCase
import com.example.app_go_play.feature.court.domain.usecase.GetCourtDetailsUseCase
import com.example.app_go_play.feature.court.domain.usecase.GetReviewsUseCase
import com.example.app_go_play.feature.court.domain.usecase.SearchCourtsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object CourtModule {

    // SỬA LỖI: Đã xóa hàm provideCourtRepository() bị trùng lặp.
    // Hilt sẽ tự động sử dụng hàm provideCourtRepository từ AppModule.

    @Provides
    @ViewModelScoped
    fun provideSearchCourtsUseCase(courtRepository: CourtRepository): SearchCourtsUseCase {
        return SearchCourtsUseCase(courtRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCourtDetailsUseCase(courtRepository: CourtRepository): GetCourtDetailsUseCase {
        return GetCourtDetailsUseCase(courtRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAvailableTimeSlotsUseCase(courtRepository: CourtRepository): GetAvailableTimeSlotsUseCase {
        return GetAvailableTimeSlotsUseCase(courtRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetReviewsUseCase(repository: CourtRepository): GetReviewsUseCase {
        return GetReviewsUseCase(repository)
    }
}
