package com.yonder.weightly.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface BillingHelper {
    fun isPurchased(sku: String): Flow<Boolean>
    fun launchBillingFlow(activity: Activity, sku: String)
}