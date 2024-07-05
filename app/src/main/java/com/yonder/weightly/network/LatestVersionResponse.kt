package com.yonder.weightly.network

import com.google.gson.annotations.SerializedName

data class LatestVersionResponse(
    @SerializedName("name") val name: String,
    @SerializedName("displayVersion") val displayVersion: String,
    @SerializedName("releaseNotes") val releaseNote: ReleaseNote,
    @SerializedName("buildVersion") val buildVersion: String,
    @SerializedName("createTime") val createTime: String,
    @SerializedName("firebaseConsoleUri") val firebaseConsoleUri: String,
    @SerializedName("testingUri") val testingUri: String,
    @SerializedName("binaryDownloadUri") val binaryDownloadUri: String,
) {
    data class ReleaseNote(
        @SerializedName("text") val text: String,
    )
}
