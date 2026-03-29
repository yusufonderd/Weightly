package com.yonder.weightly.ui

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.yonder.weightly.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.*

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupEmojiManager()
        initCrashlytics()
        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    private fun initCrashlytics() {
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }

    private fun setupEmojiManager() {
        EmojiManager.install(GoogleEmojiProvider())
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}