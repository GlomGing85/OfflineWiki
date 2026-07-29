package com.offlinewiki.data

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "link_graph",
    primaryKeys = ["sourceArticleId", "targetUrl"],
    indices = [Index("targetUrl")]
)
data class LinkGraph(
    val sourceArticleId: String,
    val targetUrl: String,
    val linkText: String,
    val isDownloaded: Boolean = false,
    val depth: Int = 1
)
