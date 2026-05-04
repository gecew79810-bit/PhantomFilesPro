package com.phantomfiles.pro.presentation.analyzer

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.ScanRepository
import com.phantomfiles.pro.presentation.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AnalyzerState(
    val isAnalyzing: Boolean = false,
    val storageTotal: Long = 0L,
    val storageUsed: Long = 0L,
    val categoryBreakdown: List<Triple<String, Long, Color>> = emptyList(),
    val topPartitions: List<FileItem> = emptyList(),
    val totalPartitionSize: Long = 0L,
    val largeFiles: List<FileItem> = emptyList(),
    val recentFiles: List<FileItem> = emptyList(),
    val emptyFolders: List<FileItem> = emptyList(),
    val appCaches: List<Pair<FileItem, Long>> = emptyList(),
    val totalCacheSize: Long = 0L,
    val residualFiles: List<FileItem> = emptyList(),
    val redundantFiles: List<FileItem> = emptyList(),
    val duplicateFiles: List<FileItem> = emptyList(),
    val duplicateWastedSize: Long = 0L,
    val totalCleanableSize: Long = 0L
)

@HiltViewModel
class StorageAnalyzerViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyzerState())
    val state: StateFlow<AnalyzerState> = _state

    fun startAnalysis() {
        if (_state.value.isAnalyzing) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isAnalyzing = true)
            val root = fileRepository.getRootPath()

            try {
                val storageInfo = fileRepository.getStorageInfo()
                _state.value = _state.value.copy(
                    storageTotal = storageInfo.totalBytes,
                    storageUsed = storageInfo.usedBytes
                )

                val imageExts = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".heic")
                val videoExts = listOf(".mp4", ".mkv", ".avi", ".mov", ".3gp", ".webm")
                val audioExts = listOf(".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a")
                val docExts = listOf(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt")
                val apkExts = listOf(".apk", ".xapk")

                val images = try { fileRepository.searchByType(root, imageExts).first() } catch (_: Exception) { emptyList() }
                val videos = try { fileRepository.searchByType(root, videoExts).first() } catch (_: Exception) { emptyList() }
                val audios = try { fileRepository.searchByType(root, audioExts).first() } catch (_: Exception) { emptyList() }
                val docs = try { fileRepository.searchByType(root, docExts).first() } catch (_: Exception) { emptyList() }
                val apks = try { fileRepository.searchByType(root, apkExts).first() } catch (_: Exception) { emptyList() }

                val imgSize = images.sumOf { it.size }
                val vidSize = videos.sumOf { it.size }
                val audSize = audios.sumOf { it.size }
                val docSize = docs.sumOf { it.size }
                val apkSize = apks.sumOf { it.size }
                val otherSize = storageInfo.usedBytes - imgSize - vidSize - audSize - docSize - apkSize

                _state.value = _state.value.copy(
                    categoryBreakdown = listOf(
                        Triple("Images", imgSize, PfAmber),
                        Triple("Videos", vidSize, PfBlue),
                        Triple("Audio", audSize, PfPink),
                        Triple("Documents", docSize, PfRed),
                        Triple("APP", apkSize, PfGreen),
                        Triple("Others", maxOf(otherSize, 0L), Color.Gray)
                    )
                )

                val topFolders = try {
                    fileRepository.listFiles(root).first()
                        .filter { it.isDirectory }
                        .map { folder ->
                            val size = try { calculateFolderSize(File(folder.path)) } catch (_: Exception) { 0L }
                            folder.copy(size = size)
                        }
                        .sortedByDescending { it.size }
                        .take(10)
                } catch (_: Exception) { emptyList() }

                _state.value = _state.value.copy(
                    topPartitions = topFolders,
                    totalPartitionSize = topFolders.sumOf { it.size }
                )

                val largeFiles = try { fileRepository.getLargeFiles(root, 50 * 1024 * 1024).first().take(20) } catch (_: Exception) { emptyList() }
                _state.value = _state.value.copy(largeFiles = largeFiles)

                val recentFiles = try { fileRepository.getRecentFiles(root, 30).first().take(20) } catch (_: Exception) { emptyList() }
                _state.value = _state.value.copy(recentFiles = recentFiles)

                val emptyFolders = try { scanRepository.findEmptyFolders(root).first() } catch (_: Exception) { emptyList() }
                _state.value = _state.value.copy(emptyFolders = emptyFolders)

                val cacheDirs = listOf("cache", "Cache", ".cache", "code_cache")
                val cacheItems = mutableListOf<Pair<FileItem, Long>>()
                try {
                    val androidDir = File("$root/Android/data")
                    if (androidDir.exists()) {
                        androidDir.listFiles()?.forEach { appDir ->
                            var totalCache = 0L
                            cacheDirs.forEach { cName ->
                                val cDir = File(appDir, cName)
                                if (cDir.exists()) totalCache += calculateFolderSize(cDir)
                            }
                            if (totalCache > 0) {
                                cacheItems.add(
                                    FileItem(path = appDir.absolutePath, name = appDir.name, size = totalCache, lastModified = appDir.lastModified(), mimeType = "", isDirectory = true) to totalCache
                                )
                            }
                        }
                    }
                } catch (_: Exception) { }
                val sortedCaches = cacheItems.sortedByDescending { it.second }
                _state.value = _state.value.copy(
                    appCaches = sortedCaches,
                    totalCacheSize = sortedCaches.sumOf { it.second }
                )

                val junkFiles = try { scanRepository.findJunkFiles(root).first() } catch (_: Exception) { emptyList() }
                val residual = junkFiles.filter { it.path.contains("cache", true) || it.path.contains("temp", true) || it.path.endsWith(".log") }
                _state.value = _state.value.copy(residualFiles = residual)

                val redundant = junkFiles.filter { it.path.endsWith(".tmp") || it.path.endsWith(".bak") || it.name.startsWith(".") }
                val oldApks = try { scanRepository.findOldApks(root).first() } catch (_: Exception) { emptyList() }
                _state.value = _state.value.copy(redundantFiles = redundant + oldApks)

                val duplicates = try { scanRepository.findDuplicates(root).first() } catch (_: Exception) { emptyList() }
                val dupeFiles = duplicates.flatMap { it.files.drop(1) }
                _state.value = _state.value.copy(
                    duplicateFiles = dupeFiles,
                    duplicateWastedSize = duplicates.sumOf { it.totalWastedSize }
                )

                val cleanable = residual.sumOf { it.size } + redundant.sumOf { it.size } + emptyFolders.size * 4096L + sortedCaches.sumOf { it.second }
                _state.value = _state.value.copy(totalCleanableSize = cleanable, isAnalyzing = false)
            } catch (_: Exception) {
                _state.value = _state.value.copy(isAnalyzing = false)
            }
        }
    }

    fun cleanAll() {
        viewModelScope.launch {
            val toDelete = _state.value.residualFiles + _state.value.redundantFiles + _state.value.emptyFolders
            toDelete.forEach { file ->
                try {
                    val f = File(file.path)
                    if (f.exists()) { if (f.isDirectory) f.deleteRecursively() else f.delete() }
                } catch (_: Exception) { }
            }
            startAnalysis()
        }
    }

    private fun calculateFolderSize(dir: File): Long {
        if (!dir.exists()) return 0L
        var total = 0L
        val stack = ArrayDeque<File>()
        stack.addLast(dir)
        var count = 0
        while (stack.isNotEmpty() && count < 5000) {
            val current = stack.removeLast()
            count++
            if (current.isFile) {
                total += current.length()
            } else {
                current.listFiles()?.forEach { stack.addLast(it) }
            }
        }
        return total
    }
}
