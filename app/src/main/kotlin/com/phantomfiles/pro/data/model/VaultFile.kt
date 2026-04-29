package com.phantomfiles.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_files")
data class VaultFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val encryptedName: String,
    val originalName: String,
    val originalPath: String,
    val fileSize: Long,
    val mimeType: String?,
    val addedAt: Long = System.currentTimeMillis()
)
