package com.yonder.weightly

object Libs {

    object Gradle {
        const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0"
    }

    object AndroidX{
        const val core = "androidx.core:core-ktx:" + Versions.coreKtx
        const val appCompat = "androidx.appcompat:appcompat:" + Versions.appCompat
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:" + Versions.constraint
        const val vmLifeCycle = "androidx.lifecycle:lifecycle-viewmodel-ktx:"+Versions.viewModel
        const val navigation = "androidx.navigation:navigation-fragment-ktx:"+Versions.navigation
        const val navigationUI = "androidx.navigation:navigation-ui-ktx:"+Versions.navigation
        const val preference = "androidx.preference:preference:"+  Versions.preference
    }

    object Google{
        const val material = "com.google.android.material:material:"+ Versions.material
        const val services = "com.google.gms:google-services:" + Versions.googleServices
    }

    object Hilt{
        const val android = "com.google.dagger:hilt-android:" + Versions.hilt
        const val compiler = "com.google.dagger:hilt-compiler:" + Versions.hilt
        const val plugin = "com.google.dagger:hilt-android-gradle-plugin:" + Versions.hilt
    }

    object Log{
        const val Timber = "com.jakewharton.timber:timber:"+ Versions.timber
    }

    object Room{
        const val ktx = "androidx.room:room-ktx:" + Versions.room
        const val runtime = "androidx.room:room-runtime:" + Versions.room
        const val compiler = "androidx.room:room-compiler:"+ Versions.room
    }

    object Firebase{
        const val boom = "com.google.firebase:firebase-bom:" +Versions.boom
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val crashlytics = "com.google.firebase:firebase-crashlytics"
        const val ads = "com.google.android.gms:play-services-ads:21.0.0"
    }

    object Network {
        const val gson: String = "com.google.code.gson:gson:" + Versions.gson
        const val retrofit = "com.squareup.retrofit2:retrofit:" + Versions.retrofit
        const val converter = "com.squareup.retrofit2:converter-gson:" + Versions.retrofit
        const val okhttp = "com.squareup.okhttp3:okhttp:" + Versions.ok_http
        const val interceptor = "com.squareup.okhttp3:logging-interceptor:" + Versions.ok_http
    }

    object Compose{
        const val runtime: String = "androidx.compose.runtime:runtime:" + Versions.Compose.runtime
        const val ui : String = "androidx.compose.ui:ui:" + Versions.Compose.ui
        const val material3 = "androidx.compose.material3:material3:" + Versions.Compose.material3
    }

    const val hawk =  "com.orhanobut:hawk:2.0.1"
    const val chart =  "com.github.PhilJay:MPAndroidChart:3.1.0"
    const val emoji =  "com.vanniktech:emoji-google:0.15.0"
    const val stateLayout = "com.github.yusufonderd:StateLayout:0.1.1"
    const val rulerView =  "com.neobaran.open:ruler-view:0.0.5"


}