package com.phantomfiles.pro.util

import java.io.File
import java.io.FileInputStream

object MagicBytesAnalyzer {

    private val SIGNATURES = mapOf(
        byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()) to "image/jpeg",
        byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte()) to "image/png",
        byteArrayOf(0x47.toByte(), 0x49.toByte(), 0x46.toByte()) to "image/gif",
        byteArrayOf(0x52.toByte(), 0x49.toByte(), 0x46.toByte(), 0x46.toByte()) to "media/riff",
        byteArrayOf(0x50.toByte(), 0x4B.toByte(), 0x03.toByte(), 0x04.toByte()) to "application/zip",
        byteArrayOf(0x25.toByte(), 0x50.toByte(), 0x44.toByte(), 0x46.toByte()) to "application/pdf",
        byteArrayOf(0x49.toByte(), 0x44.toByte(), 0x33.toByte()) to "audio/mpeg",
        byteArrayOf(0x1A.toByte(), 0x45.toByte(), 0xDF.toByte(), 0xA3.toByte()) to "video/webm",
        byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte()) to "video/mp4-candidate",
        byteArrayOf(0x66.toByte(), 0x4C.toByte(), 0x61.toByte(), 0x43.toByte()) to "audio/flac",
        byteArrayOf(0x4F.toByte(), 0x67.toByte(), 0x67.toByte(), 0x53.toByte()) to "audio/ogg",
        byteArrayOf(0x42.toByte(), 0x4D.toByte()) to "image/bmp",
    )

    fun detectRealType(file: File): String? {
        if (!file.exists() || file.isDirectory || file.length() == 0L) return null
        return try {
            val header = ByteArray(12)
            FileInputStream(file).use { stream ->
                val bytesRead = stream.read(header)
                if (bytesRead < 2) return null
            }
            for ((signature, mimeType) in SIGNATURES) {
                if (header.startsWith(signature)) {
                    if (mimeType == "video/mp4-candidate") {
                        val ftypCheck = String(header, 4, 4)
                        if (ftypCheck == "ftyp") return "video/mp4"
                        continue
                    }
                    if (mimeType == "application/zip") {
                        return refineZipType(file)
                    }
                    return mimeType
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun refineZipType(file: File): String {
        val name = file.name.lowercase()
        return when {
            name.endsWith(".apk") || name.endsWith(".xapk") -> "application/vnd.android.package-archive"
            name.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            name.endsWith(".xlsx") -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            name.endsWith(".pptx") -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            else -> "application/zip"
        }
    }

    fun isMismatch(file: File): Boolean {
        val realType = detectRealType(file) ?: return false
        val extType = mimeFromExtension(file.extension.lowercase())
        if (extType == null) return false
        val realCategory = realType.substringBefore('/')
        val extCategory = extType.substringBefore('/')
        return realCategory != extCategory
    }

    private fun mimeFromExtension(ext: String): String? = when (ext) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "webp" -> "image/webp"
        "mp4", "m4v" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "avi" -> "video/x-msvideo"
        "mov" -> "video/quicktime"
        "webm" -> "video/webm"
        "3gp" -> "video/3gpp"
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "flac" -> "audio/flac"
        "aac" -> "audio/aac"
        "ogg" -> "audio/ogg"
        "m4a" -> "audio/mp4"
        "pdf" -> "application/pdf"
        "zip" -> "application/zip"
        "apk" -> "application/vnd.android.package-archive"
        "txt" -> "text/plain"
        else -> null
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        for (i in prefix.indices) {
            if (this[i] != prefix[i]) return false
        }
        return true
    }
}
