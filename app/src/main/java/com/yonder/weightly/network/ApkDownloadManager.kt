package com.yonder.weightly.network

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object ApkDownloadManager {

    fun startDownload(context: Context, url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
        request.setDescription("Please wait...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setAllowedOverMetered(true) // Allow download over a mobile network
        request.setAllowedOverRoaming(true) // Allow download while roaming
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }
}