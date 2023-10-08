package com.yonder.weightly.utils.coroutines

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatcherModule {

    @Provides
    fun provideCoroutineDispatchers() = CoroutineDispatchers()
}
