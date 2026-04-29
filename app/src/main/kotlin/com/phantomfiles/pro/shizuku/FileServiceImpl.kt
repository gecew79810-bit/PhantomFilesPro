package com.phantomfiles.pro.shizuku

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileServiceImpl : IFileService.Stub() {

    override fun listFiles(path: String): List<String> {
        return try {
            File(path).listFiles()?.map { it.absolutePath } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun copyFile(source: String, dest: String): Boolean {
        return try {
            val src = File(source)
            val dst = File(dest, src.name)
            if (src.isDirectory) {
                src.copyRecursively(dst, overwrite = true)
            } else {
                src.copyTo(dst, overwrite = true)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun moveFile(source: String, dest: String): Boolean {
        return try {
            val src = File(source)
            val dst = File(dest, src.name)
            if (src.renameTo(dst)) {
                true
            } else {
                copyFile(source, dest) && deleteFile(source)
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.isDirectory) file.deleteRecursively() else file.delete()
        } catch (_: Exception) {
            false
        }
    }

    override fun createDirectory(path: String): Boolean {
        return try {
            File(path).mkdirs()
        } catch (_: Exception) {
            false
        }
    }

    override fun getFileSize(path: String): Long {
        return try {
            val file = File(path)
            if (file.isDirectory) {
                file.walkTopDown().filter { !it.isDirectory }.sumOf { it.length() }
            } else {
                file.length()
            }
        } catch (_: Exception) {
            0L
        }
    }

    override fun readFileContent(path: String, maxBytes: Int): String {
        return try {
            val file = File(path)
            val bytes = ByteArray(minOf(maxBytes, file.length().toInt()))
            FileInputStream(file).use { it.read(bytes) }
            String(bytes)
        } catch (_: Exception) {
            ""
        }
    }

    override fun readFileBytes(path: String, offset: Int, length: Int): ByteArray {
        return try {
            FileInputStream(File(path)).use { fis ->
                fis.skip(offset.toLong())
                val buffer = ByteArray(length)
                val read = fis.read(buffer)
                if (read < length) buffer.copyOf(read) else buffer
            }
        } catch (_: Exception) {
            ByteArray(0)
        }
    }
}
