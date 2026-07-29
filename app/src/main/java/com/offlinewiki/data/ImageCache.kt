package com.offlinewiki.data

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "image_cache",
    primaryKeys = ["imageUrl"],
    indices = [Index("articleId")]
)
data class ImageCache(
    val imageUrl: String,
    val articleId: String,
    val localPath: String,
    val width: Int = 0,
    val height: Int = 0,
    val cachedAt: Long = System.currentTimeMillis()
)
