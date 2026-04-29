package com.phantomfiles.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scanType: String,
    val foundCount: Int,
    val sizeBytes: Long,
    val scannedAt: Long = System.currentTimeMillis()
)
