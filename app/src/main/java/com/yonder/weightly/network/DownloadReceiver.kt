package com.yonder.weightly.network

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File


class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("DownloadReceiver:onReceive")
        val action = intent.action

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            val downloadedFileId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadedFileId)
            val cursor = downloadManager.query(query)

            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (cursor.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                    val index = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val uriString = cursor.getString(index)
                    val fileUri = Uri.parse(uriString)
                    val file = File(fileUri.path.orEmpty())
                    installAPK(context, file)
                }
            }
            cursor?.close()
            Timber.i("DownloadReceiver downloadedFileId:$downloadedFileId")
        }
    }

    private fun installAPK(context: Context, file: File) {
        Timber.e("installAPK File downloaded to: ${file.absolutePath}")

        if (file.exists()) {
            Timber.d("installAPK file exist:$file")

            val intent = Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            intent.setDataAndType(
                uriFromFile(context, file),
                "application/vnd.android.package-archive"
            );
            context.startActivity(intent);

        } else {
            Timber.e("installAPK File does not exist")
        }
    }

    private fun uriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}

