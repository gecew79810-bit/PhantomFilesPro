package com.phantomfiles.pro.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.DisguisedFile
import com.phantomfiles.pro.data.model.DuplicateGroup
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerState(
    val isScanning: Boolean = false,
    val scanType: String = "",
    val largeFiles: List<FileItem> = emptyList(),
    val duplicates: List<DuplicateGroup> = emptyList(),
    val disguisedFiles: List<DisguisedFile> = emptyList(),
    val junkFiles: List<FileItem> = emptyList(),
    val emptyFolders: List<FileItem> = emptyList(),
    val oldApks: List<FileItem> = emptyList(),
    val totalJunkSize: Long = 0L
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state

    fun scanLargeFiles(minSize: Long = 100 * 1024 * 1024) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "large_files")
            val files = fileRepository.getLargeFiles(fileRepository.getRootPath(), minSize).first()
            _state.value = _state.value.copy(isScanning = false, largeFiles = files)
            scanRepository.saveScanResult("large_files", files.size, files.sumOf { it.size })
        }
    }

    fun scanDuplicates() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "duplicates")
            val dupes = scanRepository.findDuplicates(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(isScanning = false, duplicates = dupes)
            scanRepository.saveScanResult("duplicates", dupes.size, dupes.sumOf { it.totalWastedSize })
        }
    }

    fun scanDisguised() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "disguised")
            val files = scanRepository.scanDisguisedFiles(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(isScanning = false, disguisedFiles = files)
            scanRepository.saveScanResult("disguised", files.size, files.sumOf { it.size })
        }
    }

    fun scanJunk() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "junk")
            val junk = scanRepository.findJunkFiles(fileRepository.getRootPath()).first()
            val empty = scanRepository.findEmptyFolders(fileRepository.getRootPath()).first()
            val apks = scanRepository.findOldApks(fileRepository.getRootPath()).first()
            val totalJunk = junk.sumOf { it.size }
            _state.value = _state.value.copy(
                isScanning = false,
                junkFiles = junk,
                emptyFolders = empty,
                oldApks = apks,
                totalJunkSize = totalJunk
            )
            scanRepository.saveScanResult("junk", junk.size + empty.size + apks.size, totalJunk)
        }
    }

    fun deleteFiles(paths: List<String>) {
        viewModelScope.launch {
            paths.forEach { fileRepository.deleteFile(it) }
        }
    }
}
