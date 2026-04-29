package com.phantomfiles.pro.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object FileHashUtil {

    fun md5(file: File): String? {
        if (!file.exists() || file.isDirectory) return null
        return try {
            val digest = MessageDigest.getInstance("MD5")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            null
        }
    }

    fun quickHash(file: File): String? {
        if (!file.exists() || file.isDirectory) return null
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val size = file.length()
            digest.update(size.toString().toByteArray())
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(minOf(4096, size.toInt()))
                val bytesRead = fis.read(buffer)
                if (bytesRead > 0) digest.update(buffer, 0, bytesRead)
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            null
        }
    }
}
