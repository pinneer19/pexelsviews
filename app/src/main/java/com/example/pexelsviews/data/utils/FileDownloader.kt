package com.example.pexelsviews.data.utils

import android.app.DownloadManager
import android.content.Context
import androidx.core.net.toUri

interface Downloader {
    fun download(url: String): Long
}

class FileDownloader(
    private val token: String, context: Context
) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun download(url: String): Long {
        val request = DownloadManager.Request(url.toUri()).setMimeType("image/jpeg")
            .setTitle("Downloading picked photo")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setDescription("Please wait").addRequestHeader("Authorization", token)
        return downloadManager.enqueue(request)
    }
}