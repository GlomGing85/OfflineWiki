package com.offlinewiki.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "folders",
    primaryKeys = ["folderId"]
)
data class Folder(
    val folderId: String,
    val name: String,
    val parentFolderId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
