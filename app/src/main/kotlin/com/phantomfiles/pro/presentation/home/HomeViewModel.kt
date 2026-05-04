package com.phantomfiles.pro.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.model.StorageInfo
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.RecycleBinRepository
import com.phantomfiles.pro.data.repository.ScanRepository
import com.phantomfiles.pro.util.FormatUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

data class HomeState(
    val storageInfo: StorageInfo = StorageInfo(0, 0, 0),
    val recentFiles: List<FileItem> = emptyList(),
    val largeFiles: List<FileItem> = emptyList(),
    val recycleBinSize: Long = 0L,
    val recycleBinCount: Int = 0,
    val isLoading: Boolean = true,
    val categories: List<CategoryInfo> = emptyList(),
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val scanStatusText: String = "",
    val scanResultText: String = "",
    val junkCount: Int = 0,
    val duplicateGroups: Int = 0,
    val disguisedCount: Int = 0,
    val hiddenCount: Int = 0,
    val junkSize: Long = 0L,
    val duplicateSize: Long = 0L
)

data class CategoryInfo(
    val name: String,
    val count: Int,
    val size: Long,
    val formattedSize: String = FormatUtils.formatSize(size)
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val recycleBinRepository: RecycleBinRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    private var scanJob: Job? = null

    init {
        loadDashboard()
    }

    fun smartScan() {
        if (_state.value.isScanning) return
        scanJob = viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanProgress = 0f, scanStatusText = "Starting Smart Scan...", scanResultText = "")
            val root = fileRepository.getRootPath()
            try {
                _state.value = _state.value.copy(scanProgress = 0.15f, scanStatusText = "Scanning junk files...")
                val junk = scanRepository.findJunkFiles(root).first()
                _state.value = _state.value.copy(junkCount = junk.size, junkSize = junk.sumOf { it.size })

                _state.value = _state.value.copy(scanProgress = 0.4f, scanStatusText = "Scanning empty folders...")
                val empty = scanRepository.findEmptyFolders(root).first()

                _state.value = _state.value.copy(scanProgress = 0.6f, scanStatusText = "Finding large files...")
                val large = fileRepository.getLargeFiles(root, 50 * 1024 * 1024).first()
                _state.value = _state.value.copy(largeFiles = large.take(20))

                _state.value = _state.value.copy(scanProgress = 0.85f, scanStatusText = "Finding duplicates...")
                val dupes = scanRepository.findDuplicates(root).first()
                _state.value = _state.value.copy(
                    duplicateGroups = dupes.size,
                    duplicateSize = dupes.sumOf { it.totalWastedSize }
                )

                _state.value = _state.value.copy(
                    scanProgress = 1f,
                    isScanning = false,
                    scanStatusText = "",
                    scanResultText = "Smart Scan done: ${junk.size} junk, ${empty.size} empty folders, ${large.size} large files, ${dupes.size} duplicate groups"
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(isScanning = false, scanStatusText = "", scanResultText = "Scan failed - check storage permission")
            }
        }
    }

    fun deepScan() {
        if (_state.value.isScanning) return
        scanJob = viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanProgress = 0f, scanStatusText = "Starting Deep Scan...", scanResultText = "")
            val root = fileRepository.getRootPath()
            try {
                _state.value = _state.value.copy(scanProgress = 0.1f, scanStatusText = "Scanning junk files...")
                val junk = scanRepository.findJunkFiles(root).first()
                _state.value = _state.value.copy(junkCount = junk.size, junkSize = junk.sumOf { it.size })

                _state.value = _state.value.copy(scanProgress = 0.25f, scanStatusText = "Scanning empty folders...")
                val empty = scanRepository.findEmptyFolders(root).first()

                _state.value = _state.value.copy(scanProgress = 0.4f, scanStatusText = "Scanning disguised files...")
                val disguised = scanRepository.scanDisguisedFiles(root).first()
                _state.value = _state.value.copy(disguisedCount = disguised.size)

                _state.value = _state.value.copy(scanProgress = 0.55f, scanStatusText = "Scanning old APKs...")
                val oldApks = scanRepository.findOldApks(root).first()

                _state.value = _state.value.copy(scanProgress = 0.7f, scanStatusText = "Finding large files...")
                val large = fileRepository.getLargeFiles(root, 50 * 1024 * 1024).first()
                _state.value = _state.value.copy(largeFiles = large.take(20))

                _state.value = _state.value.copy(scanProgress = 0.85f, scanStatusText = "Finding duplicates...")
                val dupes = scanRepository.findDuplicates(root).first()
                _state.value = _state.value.copy(
                    duplicateGroups = dupes.size,
                    duplicateSize = dupes.sumOf { it.totalWastedSize }
                )

                val hiddenCount = withContext(Dispatchers.IO) { countHiddenFiles(root) }
                _state.value = _state.value.copy(hiddenCount = hiddenCount)

                _state.value = _state.value.copy(
                    scanProgress = 1f,
                    isScanning = false,
                    scanStatusText = "",
                    scanResultText = "Deep Scan done: ${junk.size} junk, ${disguised.size} disguised, ${oldApks.size} old APKs, ${dupes.size} duplicates, $hiddenCount hidden"
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(isScanning = false, scanStatusText = "", scanResultText = "Deep Scan failed - check permission")
            }
        }
    }

    fun cleanAll() {
        if (_state.value.isScanning) return
        scanJob = viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanProgress = 0f, scanStatusText = "Cleaning junk...", scanResultText = "")
            val root = fileRepository.getRootPath()
            try {
                _state.value = _state.value.copy(scanProgress = 0.2f, scanStatusText = "Finding junk files...")
                val junk = scanRepository.findJunkFiles(root).first()

                _state.value = _state.value.copy(scanProgress = 0.4f, scanStatusText = "Finding empty folders...")
                val empty = scanRepository.findEmptyFolders(root).first()

                val all = junk + empty
                var cleaned = 0
                all.forEachIndexed { idx, file ->
                    withContext(Dispatchers.IO) {
                        try {
                            val f = File(file.path)
                            val deleted = if (f.exists()) {
                                if (f.isDirectory) f.deleteRecursively() else f.delete()
                            } else false
                            if (deleted) cleaned++
                        } catch (_: Exception) { }
                    }
                    _state.value = _state.value.copy(
                        scanProgress = 0.4f + (0.55f * (idx + 1) / all.size),
                        scanStatusText = "Cleaning ${idx + 1}/${all.size}..."
                    )
                }

                _state.value = _state.value.copy(
                    scanProgress = 1f,
                    isScanning = false,
                    scanStatusText = "",
                    scanResultText = "Cleaned $cleaned/${all.size} items"
                )
                loadDashboard()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _state.value = _state.value.copy(isScanning = false, scanResultText = "Clean failed")
            }
        }
    }

    fun cancelScan() {
        scanJob?.cancel()
        _state.value = _state.value.copy(isScanning = false, scanProgress = 0f, scanStatusText = "", scanResultText = "Scan cancelled")
    }

    private fun countHiddenFiles(root: String): Int {
        var count = 0
        val stack = ArrayDeque<File>()
        stack.addLast(File(root))
        var visited = 0
        while (stack.isNotEmpty() && visited < 3000) {
            val dir = stack.removeLast()
            visited++
            dir.listFiles()?.forEach { f ->
                if (f.name.startsWith(".")) count++
                if (f.isDirectory && visited < 3000) stack.addLast(f)
            }
        }
        return count
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val storageInfo = fileRepository.getStorageInfo()
                _state.value = _state.value.copy(storageInfo = storageInfo)

                val recentFiles = fileRepository.getRecentFiles(fileRepository.getRootPath(), 20).first()
                _state.value = _state.value.copy(recentFiles = recentFiles)

                val largeFiles = fileRepository.getLargeFiles(fileRepository.getRootPath()).first().take(10)
                _state.value = _state.value.copy(largeFiles = largeFiles)

                val rbSize = recycleBinRepository.getTotalSize()
                val rbCount = recycleBinRepository.getItemCount()
                _state.value = _state.value.copy(recycleBinSize = rbSize, recycleBinCount = rbCount)

                val categories = loadCategories()
                _state.value = _state.value.copy(categories = categories, isLoading = false)
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun loadCategories(): List<CategoryInfo> {
        val root = fileRepository.getRootPath()
        val imageExts = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".heic")
        val videoExts = listOf(".mp4", ".mkv", ".avi", ".mov", ".3gp", ".webm")
        val audioExts = listOf(".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a")
        val docExts = listOf(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt")
        val apkExts = listOf(".apk", ".xapk")

        return try {
            val images = fileRepository.searchByType(root, imageExts).first()
            val videos = fileRepository.searchByType(root, videoExts).first()
            val audios = fileRepository.searchByType(root, audioExts).first()
            val docs = fileRepository.searchByType(root, docExts).first()
            val apks = fileRepository.searchByType(root, apkExts).first()

            listOf(
                CategoryInfo("Photos", images.size, images.sumOf { it.size }),
                CategoryInfo("Videos", videos.size, videos.sumOf { it.size }),
                CategoryInfo("Audio", audios.size, audios.sumOf { it.size }),
                CategoryInfo("Documents", docs.size, docs.sumOf { it.size }),
                CategoryInfo("APKs", apks.size, apks.sumOf { it.size })
            )
        } catch (_: Exception) {
            emptyList()
        }
    }
}
