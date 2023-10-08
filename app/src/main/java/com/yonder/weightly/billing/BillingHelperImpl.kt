package com.yonder.weightly.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class BillingHelperImpl @Inject constructor(
    application: Application,
    private val defaultScope: CoroutineScope,
    knownInAppSKUs: Array<String>?,
) : PurchasesUpdatedListener, BillingClientStateListener, BillingHelper {

    private val billingClient: BillingClient

    private val knownInAppSKUs: List<String>?

    private val skuStateMap: MutableMap<String, MutableStateFlow<SkuState>> = HashMap()
    private val skuDetailsMap: MutableMap<String, MutableStateFlow<SkuDetails?>> = HashMap()

    private enum class SkuState {
        SKU_STATE_UNPURCHASED, SKU_STATE_PENDING, SKU_STATE_PURCHASED, SKU_STATE_PURCHASED_AND_ACKNOWLEDGED
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // The billing client is ready. You can query purchases here.
                // This doesn't mean that your app is set up correctly in the console -- it just
                // means that you have a connection to the Billing service.
                defaultScope.launch {
                    querySkuDetailsAsync()
                    restorePurchases()
                }
            }
        }
    }

    /**
     * Called by initializeFlows to create the various Flow objects we're planning to emit.
     * @param skuList a List<String> of SKUs representing purchases.
     */
    private fun addSkuFlows(skuList: List<String>?) {
        if (null == skuList) {
            Log.e(
                TAG,
                "addSkuFlows: " +
                        "SkuList is either null or empty."
            )
        }
        for (sku in skuList!!) {
            val skuState = MutableStateFlow(SkuState.SKU_STATE_UNPURCHASED)
            val details = MutableStateFlow<SkuDetails?>(null)
            // this initialization calls querySkuDetailsAsync() when the first subscriber appears
            details.subscriptionCount.map { count ->
                count > 0
            } // map count into active/inactive flag
                .distinctUntilChanged() // only react to true<->false changes
                .onEach { isActive -> // configure an action
                    if (isActive) {
                        querySkuDetailsAsync()
                    }
                }
                .launchIn(defaultScope) // launch it inside defaultScope

            skuStateMap[sku] = skuState
            skuDetailsMap[sku] = details
        }
    }

    /**
     * Calls the billing client functions to query sku details for the in-app SKUs.
     * SKU details are useful for displaying item names and price lists to the user, and are
     * required to make a purchase.
     */
    private suspend fun querySkuDetailsAsync() {
        if (!knownInAppSKUs.isNullOrEmpty()) {
            val skuDetailsResult = billingClient.querySkuDetails(
                SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.INAPP)
                    .setSkusList(knownInAppSKUs.toMutableList())
                    .build()
            )
            // Process the result
            onSkuDetailsResponse(skuDetailsResult.billingResult, skuDetailsResult.skuDetailsList)
        }
    }

    /**
     * This calls all the skus available and checks if they have been purchased.
     * You should call it every time the activity starts
     */
    private suspend fun restorePurchases() {
        val purchasesResult = billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP)
        val billingResult = purchasesResult.billingResult
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            handlePurchase(purchasesResult.purchasesList)
        }
    }

    /**
     * This is called right after [querySkuDetailsAsync]. It gets all the skus available
     * and get the details for all of them.
     */
    private fun onSkuDetailsResponse(billingResult: BillingResult, skuDetailsList: List<SkuDetails>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                if (skuDetailsList == null || skuDetailsList.isEmpty()) {
                    Log.e(
                        TAG,
                        "onSkuDetailsResponse: " +
                                "Found null or empty SkuDetails. " +
                                "Check to see if the SKUs you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    for (skuDetails in skuDetailsList) {
                        val sku = skuDetails.sku
                        val detailsMutableFlow = skuDetailsMap[sku]
                        detailsMutableFlow?.tryEmit(skuDetails) ?: Log.e(TAG, "Unknown sku: $sku")
                    }
                }
            }
        }
    }

    /**
     * Shows a   for an in-app purchase
     */
    override fun launchBillingFlow(activity: Activity, sku: String) {
        val skuDetails = skuDetailsMap[sku]?.value
        if (null != skuDetails) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        }
        Log.e(TAG, "SkuDetails not found for: $sku")
    }

    /**
     * This method is called right after [launchBillingFlow] which helps you complete the
     * purchase of a product
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, list: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> if (null != list) {
                handlePurchase(list)
                return
            } else Log.d(TAG, "Null Purchase List Returned from OK response!")
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(TAG, "onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Log.i(TAG, "onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(
                TAG,
                "onPurchasesUpdated: Developer error means that Google Play " +
                        "does not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The SKU product ID must match and the APK you " +
                        "are using must be signed with release keys."
            )
            else -> Log.d(TAG, "BillingResult [" + billingResult.responseCode + "]: " + billingResult.debugMessage)
        }
    }

    /**
     * This checks the purchase of a product and acknowledge it if the valid.
     * Notice that you should implement your own server to check the validity of
     * a purchase.
     */
    private fun handlePurchase(purchases: List<Purchase>?) {
        if (null != purchases) {
            for (purchase in purchases) {
                // Global check to make sure all purchases are signed correctly.
                // This check is best performed on your server.
                val purchaseState = purchase.purchaseState
                if (purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!isSignatureValid(purchase)) {
                        Log.e(
                            TAG,
                            "Invalid signature. Check to make sure your " +
                                    "public key is correct."
                        )
                        continue
                    }
                    // only set the purchased state after we've validated the signature.
                    setSkuStateFromPurchase(purchase)

                    if (!purchase.isAcknowledged) {
                        defaultScope.launch {
                            for (sku in purchase.skus) {
                                // Acknowledge item and change its state
                                val billingResult = billingClient.acknowledgePurchase(
                                    AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.purchaseToken)
                                        .build()
                                )
                                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                                    Log.e(TAG, "Error acknowledging purchase: ${purchase.skus}")
                                } else {
                                    // purchase acknowledged
                                    val skuStateFlow = skuStateMap[sku]
                                    skuStateFlow?.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                                }
                            }
                        }
                    }
                } else {
                    // purchase not purchased
                    setSkuStateFromPurchase(purchase)
                }
            }
        } else {
            Log.d(TAG, "Empty purchase list.")
        }
    }

    /**
     * This sets the state of every sku inside [skuStateMap]
     */
    private fun setSkuStateFromPurchase(purchase: Purchase) {
        if (purchase.skus.isNullOrEmpty()) {
            Log.e(TAG, "Empty list of skus")
            return
        }

        for (sku in purchase.skus) {
            val skuState = skuStateMap[sku]
            if (null == skuState) {
                Log.e(
                    TAG,
                    "Unknown SKU " + sku + ". Check to make " +
                            "sure SKU matches SKUS in the Play developer console."
                )
                continue
            }

            when (purchase.purchaseState) {
                Purchase.PurchaseState.PENDING -> skuState.tryEmit(SkuState.SKU_STATE_PENDING)
                Purchase.PurchaseState.UNSPECIFIED_STATE -> skuState.tryEmit(SkuState.SKU_STATE_UNPURCHASED)
                Purchase.PurchaseState.PURCHASED -> if (purchase.isAcknowledged) {
                    skuState.tryEmit(SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED)
                } else {
                    skuState.tryEmit(SkuState.SKU_STATE_PURCHASED)
                }
                else -> Log.e(TAG, "Purchase in unknown state: " + purchase.purchaseState)
            }
        }
    }

    /**
     * @return Flow which says whether a purchase has been purchased and acknowledge
     */
    override fun isPurchased(sku: String): Flow<Boolean> {
        val skuStateFLow = skuStateMap[sku]!!
        return skuStateFLow.map { skuState -> skuState == SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED }
    }

    /**
     * This should check the validity of your purchase with a secure server
     * making this function unnecessary
     */
    private fun isSignatureValid(purchase: Purchase): Boolean {
        return Security.verifyPurchase(purchase.originalJson, purchase.signature)
    }

    /**
     * The title of our SKU from SkuDetails.
     * @param sku to get the title from
     * @return title of the requested SKU as an observable
     * */
    fun getSkuTitle(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.title
        }
    }

    fun getSkuPrice(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.price
        }
    }

    fun getSkuDescription(sku: String): Flow<String> {
        val skuDetailsFlow = skuDetailsMap[sku]!!
        return skuDetailsFlow.mapNotNull { skuDetails ->
            skuDetails?.description
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.i(TAG, "Service disconnected")
        // reconnect the service
    }

    init {
        this.knownInAppSKUs = if (knownInAppSKUs == null) {
            ArrayList()
        } else {
            listOf(*knownInAppSKUs)
        }

        // Add flow for in app purchases
        addSkuFlows(this.knownInAppSKUs)

        // Connecting the billing client
        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        billingClient.startConnection(this)
    }

    companion object {
        private val TAG = "Monsters:" + BillingHelperImpl::class.java.simpleName

    }
}