package com.phantomfiles.pro.data.model

data class DisguisedFile(
    val path: String,
    val name: String,
    val size: Long,
    val fakeExtension: String,
    val realType: String,
    val reason: String
)

data class AppInfo(
    val packageName: String,
    val appName: String,
    val apkSize: Long,
    val dataSize: Long,
    val cacheSize: Long,
    val installDate: Long,
    val lastUsed: Long,
    val versionName: String,
    val versionCode: Long,
    val permissions: List<String>
)

data class StorageInfo(
    val totalBytes: Long,
    val usedBytes: Long,
    val freeBytes: Long,
    val appsSize: Long = 0,
    val mediaSize: Long = 0,
    val systemSize: Long = 0,
    val otherSize: Long = 0
)

data class DuplicateGroup(
    val hash: String,
    val files: List<FileItem>,
    val totalWastedSize: Long
)
