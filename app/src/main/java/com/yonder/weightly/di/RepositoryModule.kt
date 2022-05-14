package com.yonder.weightly.di

import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideWeightRepository(
        appDatabase: AppDatabase
    ): WeightRepository {
        return WeightRepository(appDatabase)
    }

}

