package com.yonder.weightly.di

import android.content.Context
import com.yonder.weightly.data.local.PreferenceManager
import com.yonder.weightly.data.local.PreferenceManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    @Singleton
    fun providePreferenceManager(
        @ApplicationContext context: Context
    ): PreferenceManager {
        return PreferenceManagerImpl(context)
    }
}
