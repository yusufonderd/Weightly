package com.yonder.weightly.di

import android.content.Context
import androidx.room.Room
import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.local.WeightDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DATABASE_NAME = "Weight_ly"

@[Module InstallIn(SingletonComponent::class)]
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(
        db: AppDatabase
    ): WeightDao = db.weightDao()
}
