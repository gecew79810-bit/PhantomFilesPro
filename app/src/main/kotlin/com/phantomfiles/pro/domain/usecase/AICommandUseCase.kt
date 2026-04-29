package com.phantomfiles.pro.domain.usecase

import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.remote.GroqApi
import com.phantomfiles.pro.data.remote.GroqMessage
import com.phantomfiles.pro.data.remote.GroqRequest
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.ScanRepository
import com.phantomfiles.pro.data.repository.SettingsRepository
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
    private val groqApi: GroqApi
) {
    fun processCommand(command: String): Flow<AIResponse> = flow {
        val cmd = command.lowercase().trim()
        when {
            cmd.contains("cache") && (cmd.contains("delete") || cmd.contains("clear") || cmd.contains("hata")) -> {
                val junkFiles = scanRepository.findJunkFiles(fileRepository.getRootPath()).first()
                val cacheFiles = junkFiles.filter { it.path.contains("cache", true) }
                emit(AIResponse(
                    message = "Found ${cacheFiles.size} cache files (${com.phantomfiles.pro.util.FormatUtils.formatSize(cacheFiles.sumOf { it.size })})",
                    action = AIAction.DELETE_FILES,
                    files = cacheFiles
                ))
            }
            cmd.contains("whatsapp") && (cmd.contains("video") || cmd.contains("media")) -> {
                val videos = fileRepository.searchByType(
                    "/storage/emulated/0/Android/media/com.whatsapp",
                    listOf(".mp4", ".3gp", ".mkv")
                ).first()
                emit(AIResponse(
                    message = "Found ${videos.size} WhatsApp videos",
                    action = AIAction.SHOW_FILES,
                    files = videos
                ))
            }
            cmd.contains("badi") || cmd.contains("large") || (cmd.contains("gb") || cmd.contains("mb")) -> {
                val minSize = extractSize(cmd)
                val largeFiles = fileRepository.getLargeFiles(fileRepository.getRootPath(), minSize).first()
                emit(AIResponse(
                    message = "Found ${largeFiles.size} files larger than ${com.phantomfiles.pro.util.FormatUtils.formatSize(minSize)}",
                    action = AIAction.SHOW_FILES,
                    files = largeFiles
                ))
            }
            cmd.contains("duplicate") || cmd.contains("copy") -> {
                val dupes = scanRepository.findDuplicates(fileRepository.getRootPath()).first()
                val allFiles = dupes.flatMap { it.files.drop(1) }
                emit(AIResponse(
                    message = "Found ${dupes.size} groups of duplicate files (${allFiles.size} duplicates)",
                    action = AIAction.SHOW_FILES,
                    files = allFiles
                ))
            }
            cmd.contains("screenshot") && (cmd.contains("purani") || cmd.contains("old") || cmd.contains("delete")) -> {
                val screenshots = fileRepository.searchByType(
                    "/storage/emulated/0/Pictures/Screenshots",
                    listOf(".png", ".jpg", ".jpeg")
                ).first()
                val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
                val oldScreenshots = screenshots.filter { it.lastModified < thirtyDaysAgo }
                emit(AIResponse(
                    message = "Found ${oldScreenshots.size} screenshots older than 30 days",
                    action = AIAction.DELETE_FILES,
                    files = oldScreenshots
                ))
            }
            cmd.contains("android/data") || cmd.contains("android data") -> {
                val files = fileRepository.listFiles("/storage/emulated/0/Android/data").first()
                emit(AIResponse(
                    message = "Android/data contains ${files.size} folders",
                    action = AIAction.SHOW_FILES,
                    files = files
                ))
            }
            cmd.contains("disguised") || cmd.contains("hidden") || cmd.contains("scan") -> {
                val disguised = scanRepository.scanDisguisedFiles(fileRepository.getRootPath()).first()
                val fileItems = disguised.map { d ->
                    FileItem(
                        path = d.path, name = d.name, size = d.size,
                        lastModified = 0, mimeType = d.realType, isDirectory = false
                    )
                }
                emit(AIResponse(
                    message = "Found ${disguised.size} disguised/hidden files",
                    action = AIAction.SHOW_FILES,
                    files = fileItems
                ))
            }
            cmd.contains("storage") || cmd.contains("report") || cmd.contains("space") -> {
                val info = fileRepository.getStorageInfo()
                emit(AIResponse(
                    message = "Storage: ${com.phantomfiles.pro.util.FormatUtils.formatSize(info.usedBytes)} used of ${com.phantomfiles.pro.util.FormatUtils.formatSize(info.totalBytes)} (${com.phantomfiles.pro.util.FormatUtils.formatSize(info.freeBytes)} free)",
                    action = AIAction.SHOW_STORAGE
                ))
            }
            else -> {
                val apiKey = settingsRepository.groqApiKey.first()
                if (apiKey.isNotBlank()) {
                    val response = groqApi.chat(
                        authorization = "Bearer $apiKey",
                        request = GroqRequest(
                            messages = listOf(
                                GroqMessage("system", "You are a file manager AI assistant. Help users manage files on their Android device. Keep responses short and actionable."),
                                GroqMessage("user", command)
                            )
                        )
                    )
                    val reply = response.choices?.firstOrNull()?.message?.content ?: "No response from AI"
                    emit(AIResponse(message = reply))
                } else {
                    emit(AIResponse(
                        message = "Command not recognized offline. Try: 'cache delete karo', 'large files dikha', 'duplicates dhundho', 'storage report'. For advanced commands, add Groq API key in Settings."
                    ))
                }
            }
        }
    }

    private fun extractSize(cmd: String): Long {
        val gbMatch = Regex("(\\d+)\\s*gb", RegexOption.IGNORE_CASE).find(cmd)
        if (gbMatch != null) return gbMatch.groupValues[1].toLong() * 1024 * 1024 * 1024
        val mbMatch = Regex("(\\d+)\\s*mb", RegexOption.IGNORE_CASE).find(cmd)
        if (mbMatch != null) return mbMatch.groupValues[1].toLong() * 1024 * 1024
        return 100 * 1024 * 1024
    }
}
