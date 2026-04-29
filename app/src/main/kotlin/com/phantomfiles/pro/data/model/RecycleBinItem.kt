package com.phantomfiles.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recycle_bin")
data class RecycleBinItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalPath: String,
    val recyclePath: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String?,
    val isDirectory: Boolean,
    val deletedAt: Long = System.currentTimeMillis()
)
