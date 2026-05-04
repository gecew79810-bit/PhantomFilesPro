package com.phantomfiles.pro.data.repository

import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.model.StorageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor() {

    fun getRootPath(): String = "/storage/emulated/0"

    fun listFiles(path: String, showHidden: Boolean = false): Flow<List<FileItem>> = flow {
        val dir = File(path)
        val files = dir.listFiles()?.filter { showHidden || !it.isHidden }?.map { it.toFileItem() } ?: emptyList()
        emit(files)
    }.flowOn(Dispatchers.IO)

    fun searchFiles(path: String, query: String, recursive: Boolean = true): Flow<List<FileItem>> = flow {
        val results = mutableListOf<FileItem>()
        searchRecursive(File(path), query, recursive, results)
        emit(results)
    }.flowOn(Dispatchers.IO)

    private fun searchRecursive(dir: File, query: String, recursive: Boolean, results: MutableList<FileItem>) {
        dir.listFiles()?.forEach { file ->
            if (file.name.contains(query, ignoreCase = true)) {
                results.add(file.toFileItem())
            }
            if (recursive && file.isDirectory) {
                try {
                    searchRecursive(file, query, true, results)
                } catch (_: Exception) { }
            }
        }
    }

    fun searchByType(basePath: String, extensions: List<String>): Flow<List<FileItem>> = flow {
        val results = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (!file.isDirectory && extensions.any { file.name.endsWith(it, true) }) {
                    results.add(file.toFileItem())
                }
                if (file.isDirectory) try { scan(file) } catch (_: Exception) { }
            }
        }
        scan(File(basePath))
        emit(results)
    }.flowOn(Dispatchers.IO)

    fun getRecentFiles(basePath: String, limit: Int = 50): Flow<List<FileItem>> = flow {
        val files = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (!file.isDirectory) files.add(file.toFileItem())
                else try { scan(file) } catch (_: Exception) { }
            }
        }
        scan(File(basePath))
        emit(files.sortedByDescending { it.lastModified }.take(limit))
    }.flowOn(Dispatchers.IO)

    fun getLargeFiles(basePath: String, minSize: Long = 100 * 1024 * 1024): Flow<List<FileItem>> = flow {
        val files = mutableListOf<FileItem>()
        fun scan(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (!file.isDirectory && file.length() >= minSize) files.add(file.toFileItem())
                else if (file.isDirectory) try { scan(file) } catch (_: Exception) { }
            }
        }
        scan(File(basePath))
        emit(files.sortedByDescending { it.size })
    }.flowOn(Dispatchers.IO)

    suspend fun copyFile(source: String, destDir: String, onProgress: (Int) -> Unit = {}): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val srcFile = File(source)
                val destFile = File(destDir, srcFile.name).let { f ->
                    if (f.exists()) File(destDir, "Copy_${srcFile.name}") else f
                }
                if (srcFile.isDirectory) {
                    copyDirectory(srcFile, destFile, onProgress)
                } else {
                    val totalBytes = srcFile.length()
                    var copiedBytes = 0L
                    FileInputStream(srcFile).use { fis ->
                        FileOutputStream(destFile).use { fos ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (fis.read(buffer).also { bytesRead = it } != -1) {
                                fos.write(buffer, 0, bytesRead)
                                copiedBytes += bytesRead
                                if (totalBytes > 0) onProgress(((copiedBytes * 100) / totalBytes).toInt())
                            }
                        }
                    }
                }
                destFile.absolutePath
            }
        }

    private fun copyDirectory(source: File, dest: File, onProgress: (Int) -> Unit) {
        dest.mkdirs()
        val allFiles = source.walkTopDown().filter { !it.isDirectory }.toList()
        allFiles.forEachIndexed { index, file ->
            val relative = file.relativeTo(source)
            val destFile = File(dest, relative.path)
            destFile.parentFile?.mkdirs()
            file.copyTo(destFile, overwrite = true)
            onProgress(((index + 1) * 100) / allFiles.size)
        }
    }

    suspend fun moveFile(source: String, destDir: String, onProgress: (Int) -> Unit = {}): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val srcFile = File(source)
                val destFile = File(destDir, srcFile.name)
                if (srcFile.renameTo(destFile)) {
                    onProgress(100)
                    destFile.absolutePath
                } else {
                    val result = copyFile(source, destDir, onProgress).getOrThrow()
                    srcFile.deleteRecursively()
                    result
                }
            }
        }

    suspend fun deleteFile(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            if (file.isDirectory) file.deleteRecursively() else file.delete()
            Unit
        }
    }

    suspend fun renameFile(path: String, newName: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            val newFile = File(file.parentFile, newName)
            if (!file.renameTo(newFile)) throw java.io.IOException("Rename failed")
            newFile.absolutePath
        }
    }

    suspend fun createFolder(parentPath: String, name: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val folder = File(parentPath, name)
            if (!folder.mkdirs()) throw java.io.IOException("Cannot create folder")
            folder.absolutePath
        }
    }

    suspend fun createFile(parentPath: String, name: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(parentPath, name)
            if (!file.createNewFile()) throw java.io.IOException("Cannot create file")
            file.absolutePath
        }
    }

    fun getStorageInfo(): StorageInfo {
        val root = File(getRootPath())
        val total = root.totalSpace
        val free = root.freeSpace
        return StorageInfo(
            totalBytes = total,
            usedBytes = total - free,
            freeBytes = free
        )
    }

    suspend fun getFileProperties(path: String): Map<String, String> = withContext(Dispatchers.IO) {
        val file = File(path)
        val props = mutableMapOf<String, String>()
        props["Name"] = file.name
        props["Path"] = file.absolutePath
        props["Size"] = com.phantomfiles.pro.util.FormatUtils.formatSize(
            if (file.isDirectory) file.walkTopDown().filter { !it.isDirectory }.sumOf { it.length() }
            else file.length()
        )
        props["Modified"] = com.phantomfiles.pro.util.FormatUtils.formatDate(file.lastModified())
        props["Readable"] = file.canRead().toString()
        props["Writable"] = file.canWrite().toString()
        if (!file.isDirectory) {
            props["MD5"] = com.phantomfiles.pro.util.FileHashUtil.md5(file) ?: "N/A"
        }
        if (file.isDirectory) {
            val count = file.listFiles()?.size ?: 0
            props["Items"] = "$count"
        }
        try {
            val attrs = Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
            props["Created"] = com.phantomfiles.pro.util.FormatUtils.formatDate(attrs.creationTime().toMillis())
        } catch (_: Exception) { }
        props
    }

    suspend fun compressToZip(paths: List<String>, destPath: String, onProgress: (Int) -> Unit = {}): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val zipFile = File(destPath)
                java.util.zip.ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                    val allFiles = paths.flatMap { path ->
                        val f = File(path)
                        if (f.isDirectory) f.walkTopDown().filter { !it.isDirectory }.map { it to it.relativeTo(f.parentFile ?: f) }.toList()
                        else listOf(f to f)
                    }
                    allFiles.forEachIndexed { index, (file, relative) ->
                        val entryName = if (file == relative) file.name else relative.path
                        zos.putNextEntry(java.util.zip.ZipEntry(entryName))
                        FileInputStream(file).use { fis ->
                            fis.copyTo(zos, 8192)
                        }
                        zos.closeEntry()
                        onProgress(((index + 1) * 100) / allFiles.size)
                    }
                }
                zipFile.absolutePath
            }
        }

    suspend fun extractZip(zipPath: String, destDir: String, onProgress: (Int) -> Unit = {}): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val destFolder = File(destDir, File(zipPath).nameWithoutExtension)
                destFolder.mkdirs()
                java.util.zip.ZipInputStream(FileInputStream(File(zipPath))).use { zis ->
                    var entry = zis.nextEntry
                    var count = 0
                    while (entry != null) {
                        val outFile = File(destFolder, entry.name)
                        if (!outFile.canonicalPath.startsWith(destFolder.canonicalPath + File.separator)) {
                            throw SecurityException("Zip path traversal detected")
                        }
                        if (entry.isDirectory) {
                            outFile.mkdirs()
                        } else {
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { fos ->
                                zis.copyTo(fos, 8192)
                            }
                        }
                        count++
                        onProgress(count)
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
                destFolder.absolutePath
            }
        }

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
