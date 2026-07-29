package com.offlinewiki.data

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "download_tasks",
    primaryKeys = ["url"]
)
data class DownloadTask(
    val url: String,
    val articleTitle: String? = null,
    val status: Int = 0,
    val retryCount: Int = 0,
    val folderId: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val depth: Int = 1
) {
    companion object {
        const val STATUS_PENDING = 0
        const val STATUS_DOWNLOADING = 1
        const val STATUS_COMPLETED = 2
        const val STATUS_FAILED = 3
    }
}
