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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val storageInfo: StorageInfo = StorageInfo(0, 0, 0),
    val recentFiles: List<FileItem> = emptyList(),
    val largeFiles: List<FileItem> = emptyList(),
    val recycleBinSize: Long = 0L,
    val recycleBinCount: Int = 0,
    val isLoading: Boolean = true,
    val categories: List<CategoryInfo> = emptyList()
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

    init {
        loadDashboard()
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
