package com.yonder.weightly.di

import com.yonder.weightly.BuildConfig
import com.yonder.weightly.network.ApiService
import com.yonder.weightly.network.FirebaseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object NetworkModule {
    val provideLoggingInterceptor: HttpLoggingInterceptor
        @[Provides Singleton] get() =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

    @[Provides Singleton]
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .apply {
                addInterceptor(loggingInterceptor)
            }.build()

    @[Provides Singleton]
    @Named("WeightlyBE")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @[Provides]
    fun provideApiService(
        @Named("WeightlyBE") retrofit: Retrofit,
    ): ApiService = retrofit.create(ApiService::class.java)

    @[Provides Singleton]
    @Named("FirebaseBE")
    fun provideRetrofitForFirebase(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL_FIREBASE_CLOUD)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @[Provides]
    fun provideFirebaseApiService(
        @Named("FirebaseBE") retrofit: Retrofit,
    ): FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}
