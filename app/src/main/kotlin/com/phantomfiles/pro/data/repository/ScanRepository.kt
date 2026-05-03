package com.phantomfiles.pro.data.repository

import com.phantomfiles.pro.data.local.ScanResultDao
import com.phantomfiles.pro.data.model.DisguisedFile
import com.phantomfiles.pro.data.model.DuplicateGroup
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.model.ScanResult
import com.phantomfiles.pro.util.FileHashUtil
import com.phantomfiles.pro.util.MagicBytesAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val scanResultDao: ScanResultDao
) {
    fun scanDisguisedFiles(basePath: String): Flow<List<DisguisedFile>> = flow {
        val results = mutableListOf<DisguisedFile>()
        scanForDisguised(File(basePath), results)
        emit(results)
    }.flowOn(Dispatchers.IO)

    private fun scanForDisguised(dir: File, results: MutableList<DisguisedFile>) {
        dir.listFiles()?.forEach { file ->
            try {
                if (file.isDirectory) {
                    if (File(file, ".nomedia").exists()) {
                        results.add(
                            DisguisedFile(
                                path = file.absolutePath,
                                name = file.name,
                                size = 0,
                                fakeExtension = ".nomedia",
                                realType = "Hidden folder",
                                reason = "Contains .nomedia file"
                            )
                        )
                    }
                    scanForDisguised(file, results)
                    return@forEach
                }
                if (file.length() == 0L && !file.name.startsWith(".")) {
                    results.add(
                        DisguisedFile(
                            path = file.absolutePath,
                            name = file.name,
                            size = 0,
                            fakeExtension = file.extension,
                            realType = "Empty/Dummy",
                            reason = "0 KB file"
                        )
                    )
                    return@forEach
                }
                if (file.name.count { it == '.' } > 1) {
                    results.add(
                        DisguisedFile(
                            path = file.absolutePath,
                            name = file.name,
                            size = file.length(),
                            fakeExtension = file.extension,
                            realType = "Double extension",
                            reason = "Multiple extensions detected"
                        )
                    )
                }
                if (MagicBytesAnalyzer.isMismatch(file)) {
                    val realType = MagicBytesAnalyzer.detectRealType(file) ?: "Unknown"
                    results.add(
                        DisguisedFile(
                            path = file.absolutePath,
                            name = file.name,
                            size = file.length(),
                            fakeExtension = file.extension,
                            realType = realType,
                            reason = "Extension doesn't match file content"
                        )
                    )
                }
            } catch (_: Exception) { }
        }
    }

    fun findDuplicates(basePath: String): Flow<List<DuplicateGroup>> = flow {
        val sizeMap = mutableMapOf<Long, MutableList<File>>()
        collectFilesBySize(File(basePath), sizeMap)
        val duplicateGroups = mutableListOf<DuplicateGroup>()
        sizeMap.filter { it.value.size > 1 && it.key > 0 }.forEach { (_, files) ->
            val hashGroups = mutableMapOf<String, MutableList<File>>()
            files.forEach { file ->
                val hash = FileHashUtil.quickHash(file)
                if (hash != null) {
                    hashGroups.getOrPut(hash) { mutableListOf() }.add(file)
                }
            }
            hashGroups.filter { it.value.size > 1 }.forEach { (hash, dupes) ->
                duplicateGroups.add(
                    DuplicateGroup(
                        hash = hash,
                        files = dupes.map { it.toFileItem() },
                        totalWastedSize = dupes.drop(1).sumOf { it.length() }
                    )
                )
            }
        }
        emit(duplicateGroups)
    }.flowOn(Dispatchers.IO)

    private fun collectFilesBySize(dir: File, sizeMap: MutableMap<Long, MutableList<File>>) {
        dir.listFiles()?.forEach { file ->
            try {
                if (file.isDirectory) collectFilesBySize(file, sizeMap)
                else sizeMap.getOrPut(file.length()) { mutableListOf() }.add(file)
            } catch (_: Exception) { }
        }
    }

    fun findJunkFiles(basePath: String): Flow<List<FileItem>> = flow {
        val junk = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                try {
                    if (file.isDirectory) {
                        if (file.name in listOf(".thumbnails", "cache", "Cache", "tmp", "temp")) {
                            file.walkTopDown().filter { !it.isDirectory }.forEach { junk.add(it.toFileItem()) }
                        } else {
                            scan(file)
                        }
                    } else if (file.extension.lowercase() in listOf("tmp", "temp", "log", "bak")) {
                        junk.add(file.toFileItem())
                    } else if (file.name.endsWith(".thumbnails") || file.name == "Thumbs.db") {
                        junk.add(file.toFileItem())
                    }
                } catch (_: Exception) { }
            }
        }
        scan(File(basePath))
        emit(junk)
    }.flowOn(Dispatchers.IO)

    fun findEmptyFolders(basePath: String): Flow<List<FileItem>> = flow {
        val empty = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    val children = file.listFiles()
                    if (children == null || children.isEmpty()) {
                        empty.add(file.toFileItem())
                    } else {
                        scan(file)
                    }
                }
            }
        }
        scan(File(basePath))
        emit(empty)
    }.flowOn(Dispatchers.IO)

    fun findOldApks(basePath: String): Flow<List<FileItem>> = flow {
        val apks = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                try {
                    if (file.isDirectory) scan(file)
                    else if (file.extension.lowercase() in listOf("apk", "xapk")) {
                        apks.add(file.toFileItem())
                    }
                } catch (_: Exception) { }
            }
        }
        scan(File(basePath))
        emit(apks)
    }.flowOn(Dispatchers.IO)

    suspend fun saveScanResult(scanType: String, foundCount: Int, sizeBytes: Long) {
        scanResultDao.insert(
            ScanResult(scanType = scanType, foundCount = foundCount, sizeBytes = sizeBytes)
        )
    }

    fun getScanHistory(): Flow<List<ScanResult>> = scanResultDao.getAllResults()

    private fun File.toFileItem(): FileItem = FileItem(
        path = absolutePath,
        name = name,
        size = if (isDirectory) 0 else length(),
        lastModified = lastModified(),
        mimeType = if (isDirectory) null else java.net.URLConnection.guessContentTypeFromName(name),
        isDirectory = isDirectory,
        isHidden = isHidden
    )
}
