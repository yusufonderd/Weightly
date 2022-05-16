package com.yonder.weightly.di

import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.repository.WeightRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideWeightRepository(
        dbDao: WeightDao
    ): WeightRepository {
        return WeightRepository(dbDao)
    }



}

