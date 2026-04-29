package com.phantomfiles.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations_log")
data class OperationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val operation: String,
    val sourcePath: String,
    val destPath: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "success"
)
