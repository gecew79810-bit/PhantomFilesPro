package com.phantomfiles.pro.data.model

data class FileItem(
    val path: String,
    val name: String,
    val size: Long,
    val lastModified: Long,
    val mimeType: String?,
    val isDirectory: Boolean,
    val isHidden: Boolean = name.startsWith("."),
    val extension: String = name.substringAfterLast('.', ""),
    val permissions: String = ""
) {
    val fileType: FileType get() = FileType.fromExtension(extension)
}

enum class FileType(val label: String) {
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DOCUMENT("Document"),
    APK("APK"),
    ARCHIVE("Archive"),
    CODE("Code"),
    FOLDER("Folder"),
    OTHER("Other");

    companion object {
        fun fromExtension(ext: String): FileType = when (ext.lowercase()) {
            "jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "heic", "heif" -> IMAGE
            "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "3gp", "m4v" -> VIDEO
            "mp3", "wav", "flac", "aac", "ogg", "m4a", "wma", "opus" -> AUDIO
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "rtf", "odt" -> DOCUMENT
            "apk", "xapk", "apks" -> APK
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz" -> ARCHIVE
            "kt", "java", "py", "js", "ts", "html", "css", "xml", "json", "c", "cpp", "h", "rs", "go", "rb", "php", "sh" -> CODE
            else -> OTHER
        }
    }
}
