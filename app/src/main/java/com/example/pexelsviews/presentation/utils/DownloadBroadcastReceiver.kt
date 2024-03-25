package com.example.pexelsviews.presentation.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class DownloadBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("Range")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager =
                context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> makeToast(
                        context,
                        "Image was downloaded successfully"
                    )

                    DownloadManager.STATUS_FAILED -> {
                        val reason =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                        makeToast(context, reason.reasonToString())
                    }
                }
            }
            cursor.close()
        }
    }

    private fun Int.reasonToString(): String {
        return when (this) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Can't resume the download"
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "No external storage device was found"
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "The requested destination file already exists"
            DownloadManager.ERROR_FILE_ERROR -> "Storage issue"
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "Error occurred while processing HTTP data"
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "There was insufficient storage space"
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "There were too many redirects"
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Download manager can't handle received HTTP code"
            else -> "Unknown error"
        }
    }

    private fun makeToast(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}