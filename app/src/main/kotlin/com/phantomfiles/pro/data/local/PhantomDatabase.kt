package com.phantomfiles.pro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phantomfiles.pro.data.model.Bookmark
import com.phantomfiles.pro.data.model.FilesCache
import com.phantomfiles.pro.data.model.OperationLog
import com.phantomfiles.pro.data.model.RecycleBinItem
import com.phantomfiles.pro.data.model.ScanResult
import com.phantomfiles.pro.data.model.VaultFile

@Database(
    entities = [
        FilesCache::class,
        RecycleBinItem::class,
        VaultFile::class,
        ScanResult::class,
        Bookmark::class,
        OperationLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PhantomDatabase : RoomDatabase() {
    abstract fun filesCacheDao(): FilesCacheDao
    abstract fun recycleBinDao(): RecycleBinDao
    abstract fun vaultDao(): VaultDao
    abstract fun scanResultDao(): ScanResultDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun operationLogDao(): OperationLogDao
}
