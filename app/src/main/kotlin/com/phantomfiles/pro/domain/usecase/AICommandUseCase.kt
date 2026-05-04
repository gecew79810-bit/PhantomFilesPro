package com.phantomfiles.pro.domain.usecase

import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.remote.GeminiApi
import com.phantomfiles.pro.data.remote.GeminiContent
import com.phantomfiles.pro.data.remote.GeminiPart
import com.phantomfiles.pro.data.remote.GeminiRequest
import com.phantomfiles.pro.data.remote.GroqApi
import com.phantomfiles.pro.data.remote.GroqMessage
import com.phantomfiles.pro.data.remote.GroqRequest
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.ScanRepository
import com.phantomfiles.pro.data.repository.SettingsRepository
import com.phantomfiles.pro.util.FormatUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class AIResponse(
    val message: String,
    val action: AIAction = AIAction.NONE,
    val files: List<FileItem> = emptyList()
)

enum class AIAction {
    NONE, SHOW_FILES, DELETE_FILES, SCAN, SHOW_STORAGE
}

class AICommandUseCase @Inject constructor(
    private val fileRepository: FileRepository,
    private val scanRepository: ScanRepository,
    private val settingsRepository: SettingsRepository,
    private val groqApi: GroqApi,
    private val geminiApi: GeminiApi
) {
    private val cachePatterns = listOf("cache", "cach", "kesh", "temp file", "temporary")
    private val deletePatterns = listOf("delete", "remove", "hata", "saaf", "clean", "clear", "mita", "erase", "karo saaf")
    private val largePatterns = listOf("large", "big", "badi", "bada", "heavy", "huge", "bharee", "size", "space le rahi")
    private val duplicatePatterns = listOf("duplicate", "copy", "dohri", "same", "naqal", "double", "similar", "repeated")
    private val screenshotPatterns = listOf("screenshot", "screen shot", "ss", "screen capture")
    private val oldPatterns = listOf("old", "purani", "purane", "pahle", "earlier", "before")
    private val whatsappPatterns = listOf("whatsapp", "wa", "watsapp", "watsap")
    private val videoPatterns = listOf("video", "vid", "movie", "film", "recording")
    private val imagePatterns = listOf("image", "photo", "pic", "picture", "tasveer", "foto", "gallery")
    private val audioPatterns = listOf("audio", "music", "song", "gaana", "mp3", "sound")
    private val documentPatterns = listOf("document", "doc", "pdf", "file", "dastavez")
    private val downloadPatterns = listOf("download", "downloaded")
    private val storagePatterns = listOf("storage", "space", "memory", "jagah", "report", "usage", "kitni")
    private val scanPatterns = listOf("scan", "check", "find hidden", "disguised", "suspicious", "chhupa")
    private val junkPatterns = listOf("junk", "garbage", "kachra", "waste", "bekar", "useless", "residual", "leftover")
    private val emptyPatterns = listOf("empty folder", "khali folder", "blank folder")
    private val apkPatterns = listOf("apk", "app file", "installer")
    private val recentPatterns = listOf("recent", "latest", "new file", "nayi file", "haal", "last")
    private val androidDataPatterns = listOf("android/data", "android data", "app data", "private folder")
    private val helpPatterns = listOf("help", "madad", "kya kar sakt", "what can", "commands", "features")
    private val findFilePatterns = listOf("find", "dhundh", "khoj", "search", "locate", "where is", "kahan", "kaha")

    fun processCommand(command: String): Flow<AIResponse> = flow {
        val cmd = command.lowercase().trim()
        val intent = detectIntent(cmd)

        when (intent) {
            Intent.CACHE_CLEAN -> {
                val junkFiles = scanRepository.findJunkFiles(fileRepository.getRootPath()).first()
                val cacheFiles = junkFiles.filter { it.path.contains("cache", true) || it.path.contains("Cache", false) }
                emit(AIResponse(
                    message = "Found ${cacheFiles.size} cache files (${FormatUtils.formatSize(cacheFiles.sumOf { it.size })}). Ready to clean.",
                    action = AIAction.DELETE_FILES,
                    files = cacheFiles
                ))
            }
            Intent.WHATSAPP_MEDIA -> {
                val exts = when {
                    matchesAny(cmd, videoPatterns) -> listOf(".mp4", ".3gp", ".mkv", ".avi")
                    matchesAny(cmd, imagePatterns) -> listOf(".jpg", ".jpeg", ".png", ".gif", ".webp")
                    matchesAny(cmd, audioPatterns) -> listOf(".mp3", ".opus", ".m4a", ".aac")
                    else -> listOf(".mp4", ".3gp", ".jpg", ".jpeg", ".png", ".gif", ".mp3", ".opus")
                }
                val mediaType = when {
                    matchesAny(cmd, videoPatterns) -> "videos"
                    matchesAny(cmd, imagePatterns) -> "photos"
                    matchesAny(cmd, audioPatterns) -> "audio files"
                    else -> "media files"
                }
                val basePaths = listOf(
                    "/storage/emulated/0/Android/media/com.whatsapp",
                    "/storage/emulated/0/WhatsApp/Media",
                    "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media"
                )
                val results = mutableListOf<FileItem>()
                for (path in basePaths) {
                    try { results.addAll(fileRepository.searchByType(path, exts).first()) } catch (_: Exception) { }
                }
                emit(AIResponse(
                    message = "Found ${results.size} WhatsApp $mediaType (${FormatUtils.formatSize(results.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = results.sortedByDescending { it.size }
                ))
            }
            Intent.LARGE_FILES -> {
                val minSize = extractSize(cmd)
                val largeFiles = fileRepository.getLargeFiles(fileRepository.getRootPath(), minSize).first()
                emit(AIResponse(
                    message = "Found ${largeFiles.size} files larger than ${FormatUtils.formatSize(minSize)} (Total: ${FormatUtils.formatSize(largeFiles.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = largeFiles
                ))
            }
            Intent.DUPLICATES -> {
                val dupes = scanRepository.findDuplicates(fileRepository.getRootPath()).first()
                val allFiles = dupes.flatMap { it.files.drop(1) }
                val wasted = dupes.sumOf { it.totalWastedSize }
                emit(AIResponse(
                    message = "Found ${dupes.size} groups of duplicate files (${allFiles.size} duplicates, ${FormatUtils.formatSize(wasted)} wasted space)",
                    action = AIAction.SHOW_FILES,
                    files = allFiles
                ))
            }
            Intent.OLD_SCREENSHOTS -> {
                val searchPaths = listOf(
                    "/storage/emulated/0/Pictures/Screenshots",
                    "/storage/emulated/0/DCIM/Screenshots",
                    "/storage/emulated/0/Screenshots"
                )
                val allScreenshots = mutableListOf<FileItem>()
                for (path in searchPaths) {
                    try {
                        allScreenshots.addAll(fileRepository.searchByType(path, listOf(".png", ".jpg", ".jpeg", ".webp")).first())
                    } catch (_: Exception) { }
                }
                val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                val old = allScreenshots.filter { it.lastModified < thirtyDaysAgo }
                emit(AIResponse(
                    message = "Found ${old.size} screenshots older than 30 days (${FormatUtils.formatSize(old.sumOf { it.size })})",
                    action = AIAction.DELETE_FILES,
                    files = old.sortedBy { it.lastModified }
                ))
            }
            Intent.ANDROID_DATA -> {
                val files = try { fileRepository.listFiles("/storage/emulated/0/Android/data").first() } catch (_: Exception) { emptyList() }
                emit(AIResponse(
                    message = "Android/data contains ${files.size} app folders. Note: Full access requires Shizuku.",
                    action = AIAction.SHOW_FILES,
                    files = files
                ))
            }
            Intent.DISGUISED_SCAN -> {
                val disguised = scanRepository.scanDisguisedFiles(fileRepository.getRootPath()).first()
                val fileItems = disguised.map { d ->
                    FileItem(path = d.path, name = "${d.name} [${d.realType}]", size = d.size, lastModified = 0, mimeType = d.realType, isDirectory = false)
                }
                emit(AIResponse(
                    message = "Found ${disguised.size} suspicious files:\n" + disguised.take(5).joinToString("\n") { "• ${it.name}: ${it.reason}" },
                    action = AIAction.SHOW_FILES,
                    files = fileItems
                ))
            }
            Intent.STORAGE_REPORT -> {
                val info = fileRepository.getStorageInfo()
                val usedPct = if (info.totalBytes > 0) ((info.usedBytes * 100) / info.totalBytes).toInt() else 0
                emit(AIResponse(
                    message = buildString {
                        append("📊 Storage Report:\n")
                        append("• Total: ${FormatUtils.formatSize(info.totalBytes)}\n")
                        append("• Used: ${FormatUtils.formatSize(info.usedBytes)} ($usedPct%)\n")
                        append("• Free: ${FormatUtils.formatSize(info.freeBytes)}\n")
                        if (usedPct > 90) append("\n⚠️ Storage almost full! Consider cleaning junk files.")
                        else if (usedPct > 70) append("\n💡 Tip: Run a junk scan to free space.")
                    },
                    action = AIAction.SHOW_STORAGE
                ))
            }
            Intent.JUNK_CLEAN -> {
                val junk = scanRepository.findJunkFiles(fileRepository.getRootPath()).first()
                val empty = scanRepository.findEmptyFolders(fileRepository.getRootPath()).first()
                val allJunk = junk + empty
                emit(AIResponse(
                    message = "Found ${junk.size} junk files and ${empty.size} empty folders (${FormatUtils.formatSize(junk.sumOf { it.size })} recoverable)",
                    action = AIAction.DELETE_FILES,
                    files = allJunk
                ))
            }
            Intent.EMPTY_FOLDERS -> {
                val empty = scanRepository.findEmptyFolders(fileRepository.getRootPath()).first()
                emit(AIResponse(
                    message = "Found ${empty.size} empty folders that can be safely removed",
                    action = AIAction.DELETE_FILES,
                    files = empty
                ))
            }
            Intent.OLD_APKS -> {
                val apks = scanRepository.findOldApks(fileRepository.getRootPath()).first()
                emit(AIResponse(
                    message = "Found ${apks.size} APK files (${FormatUtils.formatSize(apks.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = apks.sortedByDescending { it.size }
                ))
            }
            Intent.FIND_IMAGES -> {
                val images = fileRepository.searchByType(
                    fileRepository.getRootPath(), listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".heic", ".bmp")
                ).first()
                emit(AIResponse(
                    message = "Found ${images.size} images (${FormatUtils.formatSize(images.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = images.sortedByDescending { it.lastModified }.take(100)
                ))
            }
            Intent.FIND_VIDEOS -> {
                val videos = fileRepository.searchByType(
                    fileRepository.getRootPath(), listOf(".mp4", ".mkv", ".avi", ".mov", ".3gp", ".webm")
                ).first()
                emit(AIResponse(
                    message = "Found ${videos.size} videos (${FormatUtils.formatSize(videos.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = videos.sortedByDescending { it.size }
                ))
            }
            Intent.FIND_AUDIO -> {
                val audio = fileRepository.searchByType(
                    fileRepository.getRootPath(), listOf(".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a")
                ).first()
                emit(AIResponse(
                    message = "Found ${audio.size} audio files (${FormatUtils.formatSize(audio.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = audio.sortedByDescending { it.lastModified }.take(100)
                ))
            }
            Intent.FIND_DOCUMENTS -> {
                val docs = fileRepository.searchByType(
                    fileRepository.getRootPath(), listOf(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".csv")
                ).first()
                emit(AIResponse(
                    message = "Found ${docs.size} documents (${FormatUtils.formatSize(docs.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = docs.sortedByDescending { it.lastModified }.take(100)
                ))
            }
            Intent.FIND_DOWNLOADS -> {
                val downloads = try {
                    fileRepository.listFiles("/storage/emulated/0/Download").first()
                } catch (_: Exception) { emptyList() }
                emit(AIResponse(
                    message = "Downloads folder has ${downloads.size} items (${FormatUtils.formatSize(downloads.sumOf { it.size })})",
                    action = AIAction.SHOW_FILES,
                    files = downloads.sortedByDescending { it.lastModified }
                ))
            }
            Intent.RECENT_FILES -> {
                val recent = fileRepository.getRecentFiles(fileRepository.getRootPath(), 30).first()
                emit(AIResponse(
                    message = "Here are your ${recent.size} most recent files",
                    action = AIAction.SHOW_FILES,
                    files = recent
                ))
            }
            Intent.HELP -> {
                emit(AIResponse(
                    message = buildString {
                        append("🤖 Main yeh sab kar sakta hoon:\n\n")
                        append("📁 Files:\n")
                        append("• \"Large files dikha\" / \"Show big files\"\n")
                        append("• \"Duplicate photos hata do\"\n")
                        append("• \"Recent files\" / \"Downloads dikha\"\n")
                        append("• \"Find images/videos/audio/documents\"\n\n")
                        append("🧹 Cleanup:\n")
                        append("• \"Cache delete karo\" / \"Clear cache\"\n")
                        append("• \"Junk files saaf karo\"\n")
                        append("• \"Empty folders hata do\"\n")
                        append("• \"Old screenshots delete karo\"\n")
                        append("• \"Old APK files dhundho\"\n\n")
                        append("🔍 Scan:\n")
                        append("• \"Disguised files scan karo\"\n")
                        append("• \"Storage report dikha\"\n")
                        append("• \"WhatsApp videos dhundho\"\n")
                        append("• \"Android/data mein kya hai\"\n\n")
                        append("💡 Groq API key settings mein add karo for advanced commands!")
                    }
                ))
            }
            Intent.FIND_FILE_BY_NAME -> {
                val query = cmd
                    .replace(Regex("\\b(find|dhundh|khoj|search|locate|where is|kahan|kaha|kar|karo|do|file|named|naam)\\b"), "")
                    .trim()
                    .ifEmpty { cmd }
                if (query.length >= 2) {
                    val results = fileRepository.searchFiles(fileRepository.getRootPath(), query).first()
                    emit(AIResponse(
                        message = if (results.isNotEmpty()) "Found ${results.size} files matching \"$query\" (${FormatUtils.formatSize(results.sumOf { it.size })})" else "No files found matching \"$query\"",
                        action = AIAction.SHOW_FILES,
                        files = results.sortedByDescending { it.lastModified }.take(100)
                    ))
                } else {
                    emit(AIResponse(message = "Please specify what to search for. Example: \"find report.pdf\" or \"dhundh screenshot\""))
                }
            }
            Intent.UNKNOWN -> {
                val groqKey = try { settingsRepository.groqApiKey.first() } catch (_: Exception) { "" }
                val geminiKey = try { settingsRepository.geminiApiKey.first() } catch (_: Exception) { "" }
                if (geminiKey.isNotBlank()) {
                    try {
                        val response = geminiApi.generate(
                            apiKey = geminiKey,
                            request = GeminiRequest(
                                contents = listOf(
                                    GeminiContent(parts = listOf(
                                        GeminiPart("You are PhantomFiles AI, an Android file manager assistant. Help users manage files. Keep responses short (2-3 lines max), actionable, and in the same language the user uses. If Hindi/Hinglish, reply in Hinglish.\n\nUser: $command")
                                    ))
                                )
                            )
                        )
                        val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from Gemini"
                        emit(AIResponse(message = reply))
                    } catch (e: Exception) {
                        emit(AIResponse(message = "Gemini error: ${e.message}\n\nTry offline commands: 'cache clear karo', 'large files dikha'"))
                    }
                } else if (groqKey.isNotBlank()) {
                    try {
                        val response = groqApi.chat(
                            authorization = "Bearer $groqKey",
                            request = GroqRequest(
                                messages = listOf(
                                    GroqMessage("system", "You are PhantomFiles AI, an Android file manager assistant. Help users manage files. Keep responses short (2-3 lines max), actionable, and in the same language the user uses. If they speak Hindi/Hinglish, reply in Hinglish."),
                                    GroqMessage("user", command)
                                )
                            )
                        )
                        val reply = response.choices?.firstOrNull()?.message?.content ?: "No response from AI"
                        emit(AIResponse(message = reply))
                    } catch (e: Exception) {
                        emit(AIResponse(message = "API error: ${e.message}\n\nTry offline commands: 'cache clear karo', 'large files dikha'"))
                    }
                } else {
                    emit(AIResponse(
                        message = "Yeh command samajh nahi aaya. Try:\n• \"cache delete karo\"\n• \"large files dikha\"\n• \"find [filename]\"\n• \"storage report dikha\"\n• \"help\" for all commands\n\nFor advanced AI: Add Gemini or Groq API key in Settings"
                    ))
                }
            }
        }
    }

    private enum class Intent {
        CACHE_CLEAN, WHATSAPP_MEDIA, LARGE_FILES, DUPLICATES, OLD_SCREENSHOTS,
        ANDROID_DATA, DISGUISED_SCAN, STORAGE_REPORT, JUNK_CLEAN, EMPTY_FOLDERS,
        OLD_APKS, FIND_IMAGES, FIND_VIDEOS, FIND_AUDIO, FIND_DOCUMENTS,
        FIND_DOWNLOADS, RECENT_FILES, FIND_FILE_BY_NAME, HELP, UNKNOWN
    }

    private val fileExtensionRegex = Regex("\\.[a-zA-Z0-9]{1,6}(\\s|$)")

    private fun looksLikeFileSearch(cmd: String): Boolean =
        matchesAny(cmd, findFilePatterns) && fileExtensionRegex.containsMatchIn(cmd)

    private fun detectIntent(cmd: String): Intent = when {
        matchesAny(cmd, helpPatterns) -> Intent.HELP
        looksLikeFileSearch(cmd) -> Intent.FIND_FILE_BY_NAME
        matchesAny(cmd, cachePatterns) && matchesAny(cmd, deletePatterns + listOf("karo", "do", "kar", "clean")) -> Intent.CACHE_CLEAN
        matchesAny(cmd, cachePatterns) -> Intent.CACHE_CLEAN
        matchesAny(cmd, whatsappPatterns) -> Intent.WHATSAPP_MEDIA
        matchesAny(cmd, screenshotPatterns) && matchesAny(cmd, oldPatterns + deletePatterns) -> Intent.OLD_SCREENSHOTS
        matchesAny(cmd, duplicatePatterns) -> Intent.DUPLICATES
        matchesAny(cmd, emptyPatterns) || (matchesAny(cmd, listOf("empty", "khali")) && cmd.contains("folder")) -> Intent.EMPTY_FOLDERS
        matchesAny(cmd, junkPatterns) -> Intent.JUNK_CLEAN
        matchesAny(cmd, apkPatterns) && !cmd.contains("install") -> Intent.OLD_APKS
        matchesAny(cmd, androidDataPatterns) -> Intent.ANDROID_DATA
        matchesAny(cmd, scanPatterns) && (matchesAny(cmd, listOf("disguised", "hidden", "chhupa", "fake"))) -> Intent.DISGUISED_SCAN
        matchesAny(cmd, largePatterns) || cmd.contains("gb") || cmd.contains("mb") -> Intent.LARGE_FILES
        matchesAny(cmd, storagePatterns) -> Intent.STORAGE_REPORT
        matchesAny(cmd, downloadPatterns) -> Intent.FIND_DOWNLOADS
        matchesAny(cmd, recentPatterns) -> Intent.RECENT_FILES
        matchesAny(cmd, imagePatterns) && !matchesAny(cmd, whatsappPatterns) -> Intent.FIND_IMAGES
        matchesAny(cmd, videoPatterns) && !matchesAny(cmd, whatsappPatterns) -> Intent.FIND_VIDEOS
        matchesAny(cmd, audioPatterns) && !matchesAny(cmd, whatsappPatterns) -> Intent.FIND_AUDIO
        matchesAny(cmd, documentPatterns) && !cmd.contains("android") -> Intent.FIND_DOCUMENTS
        matchesAny(cmd, findFilePatterns) -> Intent.FIND_FILE_BY_NAME
        else -> Intent.UNKNOWN
    }

    private fun matchesAny(text: String, patterns: List<String>): Boolean =
        patterns.any { text.contains(it) }

    private fun extractSize(cmd: String): Long {
        val gbMatch = Regex("(\\d+)\\s*gb", RegexOption.IGNORE_CASE).find(cmd)
        if (gbMatch != null) return gbMatch.groupValues[1].toLong() * 1024 * 1024 * 1024
        val mbMatch = Regex("(\\d+)\\s*mb", RegexOption.IGNORE_CASE).find(cmd)
        if (mbMatch != null) return mbMatch.groupValues[1].toLong() * 1024 * 1024
        return 100L * 1024 * 1024
    }
}
