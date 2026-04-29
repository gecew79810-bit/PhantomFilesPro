package com.phantomfiles.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files_cache")
data class FilesCache(
    @PrimaryKey val path: String,
    val name: String,
    val size: Long,
    val type: String,
    val modified: Long,
    val hash: String = "",
    val partition: String = "internal"
)
