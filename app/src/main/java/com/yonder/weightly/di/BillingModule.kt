package com.yonder.weightly.di



import android.content.Context
import com.yonder.weightly.billing.BillingHelper
import com.yonder.weightly.billing.BillingHelperImpl
import com.yonder.weightly.ui.App
import com.yonder.weightly.utils.Constants

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingHelper(
        @ApplicationContext appContext: Context,
        defaultScope: CoroutineScope
    ): BillingHelper {
        return BillingHelperImpl(
            (appContext as App),
            defaultScope,
            arrayOf(Constants.PREMIUM_ACCOUNT)
        )
    }
}