package com.phantomfiles.pro.data.repository

import android.content.Context
import com.phantomfiles.pro.data.local.RecycleBinDao
import com.phantomfiles.pro.data.model.RecycleBinItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecycleBinRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recycleBinDao: RecycleBinDao
) {
    private val recycleBinDir: File
        get() = File(context.filesDir, "recycle_bin").also { it.mkdirs() }

    fun getAllItems(): Flow<List<RecycleBinItem>> = recycleBinDao.getAllItems()

    suspend fun moveToRecycleBin(filePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = File(filePath)
            val recycledName = "${System.currentTimeMillis()}_${sourceFile.name}"
            val destFile = File(recycleBinDir, recycledName)

            if (sourceFile.isDirectory) {
                sourceFile.copyRecursively(destFile, overwrite = true)
                sourceFile.deleteRecursively()
            } else {
                sourceFile.copyTo(destFile, overwrite = true)
                sourceFile.delete()
            }

            recycleBinDao.insert(
                RecycleBinItem(
                    originalPath = filePath,
                    recyclePath = destFile.absolutePath,
                    fileName = sourceFile.name,
                    fileSize = destFile.length(),
                    mimeType = java.net.URLConnection.guessContentTypeFromName(sourceFile.name),
                    isDirectory = sourceFile.isDirectory
                )
            )
            Unit
        }
    }

    suspend fun restoreItem(item: RecycleBinItem): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val recycledFile = File(item.recyclePath)
            val originalFile = File(item.originalPath)
            originalFile.parentFile?.mkdirs()
            if (recycledFile.isDirectory) {
                recycledFile.copyRecursively(originalFile, overwrite = true)
                recycledFile.deleteRecursively()
            } else {
                recycledFile.copyTo(originalFile, overwrite = true)
                recycledFile.delete()
            }
            recycleBinDao.delete(item)
        }
    }

    suspend fun permanentlyDelete(item: RecycleBinItem): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(item.recyclePath)
            if (file.isDirectory) file.deleteRecursively() else file.delete()
            recycleBinDao.delete(item)
        }
    }

    suspend fun emptyRecycleBin(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            recycleBinDir.deleteRecursively()
            recycleBinDir.mkdirs()
            recycleBinDao.clearAll()
        }
    }

    suspend fun autoClean(maxAgeDays: Int = 30) = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - (maxAgeDays * 24 * 60 * 60 * 1000L)
        recycleBinDao.deleteOlderThan(cutoff)
    }

    suspend fun getTotalSize(): Long = recycleBinDao.getTotalSize() ?: 0L

    suspend fun getItemCount(): Int = recycleBinDao.getItemCount()

    fun search(query: String): Flow<List<RecycleBinItem>> = recycleBinDao.search(query)
}
