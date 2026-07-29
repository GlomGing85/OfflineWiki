package com.offlinewiki.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "articles",
    primaryKeys = ["articleId"],
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["folderId"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("folderId")]
)
data class Article(
    val articleId: String,
    val title: String,
    val url: String,
    val folderId: String? = null,
    val markdownContent: String,
    val htmlContent: String? = null,
    val downloadedAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val imageCount: Int = 0
)
