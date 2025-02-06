package com.yonder.weightly.di

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@DelicateCoroutinesApi
object KotlinModule {

    @[Provides Singleton]

    fun provideCoroutineScope(): CoroutineScope {
        return GlobalScope
    }

    @[Provides Singleton]

    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager {
        return context.packageManager
    }


    @[Provides Singleton]
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @[Provides Singleton]
    fun provideContext(
        @ApplicationContext context: Context
    ): Context {
        return context
    }
}