package com.yonder.weightly.network

import com.google.gson.annotations.SerializedName

data class SendEmailRequest(
    @SerializedName("app_name") val appName: String,
    @SerializedName("email") val email: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("content") val content: String
)