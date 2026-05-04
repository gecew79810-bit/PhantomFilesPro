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
import java.io.File
import javax.inject.Inject

data class ScannerState(
    val isScanning: Boolean = false,
    val scanType: String = "",
    val scanProgress: Int = 0,
    val scanStatus: String = "",
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
            _state.value = _state.value.copy(isScanning = true, scanType = "large files", scanProgress = 0, scanStatus = "Starting scan...")
            _state.value = _state.value.copy(scanProgress = 10, scanStatus = "Scanning storage...")
            val files = fileRepository.getLargeFiles(fileRepository.getRootPath(), minSize).first()
            _state.value = _state.value.copy(scanProgress = 90, scanStatus = "Found ${files.size} files")
            _state.value = _state.value.copy(isScanning = false, largeFiles = files, scanProgress = 100, scanStatus = "Complete")
            scanRepository.saveScanResult("large_files", files.size, files.sumOf { it.size })
        }
    }

    fun scanDuplicates() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "duplicates", scanProgress = 0, scanStatus = "Starting scan...")
            _state.value = _state.value.copy(scanProgress = 10, scanStatus = "Computing file hashes...")
            val dupes = scanRepository.findDuplicates(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(scanProgress = 90, scanStatus = "Found ${dupes.size} groups")
            _state.value = _state.value.copy(isScanning = false, duplicates = dupes, scanProgress = 100, scanStatus = "Complete")
            scanRepository.saveScanResult("duplicates", dupes.size, dupes.sumOf { it.totalWastedSize })
        }
    }

    fun scanDisguised() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "disguised", scanProgress = 0, scanStatus = "Starting scan...")
            _state.value = _state.value.copy(scanProgress = 10, scanStatus = "Checking magic bytes...")
            val files = scanRepository.scanDisguisedFiles(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(scanProgress = 90, scanStatus = "Found ${files.size} disguised files")
            _state.value = _state.value.copy(isScanning = false, disguisedFiles = files, scanProgress = 100, scanStatus = "Complete")
            scanRepository.saveScanResult("disguised", files.size, files.sumOf { it.size })
        }
    }

    fun scanJunk() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, scanType = "junk", scanProgress = 0, scanStatus = "Starting scan...")
            _state.value = _state.value.copy(scanProgress = 20, scanStatus = "Finding cache files...")
            val junk = scanRepository.findJunkFiles(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(scanProgress = 50, scanStatus = "Finding empty folders...")
            val empty = scanRepository.findEmptyFolders(fileRepository.getRootPath()).first()
            _state.value = _state.value.copy(scanProgress = 75, scanStatus = "Finding old APKs...")
            val apks = scanRepository.findOldApks(fileRepository.getRootPath()).first()
            val totalJunk = junk.sumOf { it.size }
            _state.value = _state.value.copy(scanProgress = 90, scanStatus = "Found ${junk.size + empty.size + apks.size} items")
            _state.value = _state.value.copy(
                isScanning = false,
                junkFiles = junk,
                emptyFolders = empty,
                oldApks = apks,
                totalJunkSize = totalJunk,
                scanProgress = 100,
                scanStatus = "Complete"
            )
            scanRepository.saveScanResult("junk", junk.size + empty.size + apks.size, totalJunk)
        }
    }

    fun deleteFile(path: String) {
        viewModelScope.launch {
            try {
                val file = File(path)
                if (file.exists()) {
                    if (file.isDirectory) file.deleteRecursively() else file.delete()
                }
                val updatedDuplicates = _state.value.duplicates.map { group ->
                    val filteredFiles = group.files.filter { it.path != path }
                    group.copy(files = filteredFiles, totalWastedSize = if (filteredFiles.size > 1) filteredFiles.drop(1).sumOf { it.size } else 0L)
                }.filter { it.files.size > 1 }
                _state.value = _state.value.copy(
                    junkFiles = _state.value.junkFiles.filter { it.path != path },
                    largeFiles = _state.value.largeFiles.filter { it.path != path },
                    disguisedFiles = _state.value.disguisedFiles.filter { it.path != path },
                    emptyFolders = _state.value.emptyFolders.filter { it.path != path },
                    oldApks = _state.value.oldApks.filter { it.path != path },
                    duplicates = updatedDuplicates,
                    totalJunkSize = _state.value.junkFiles.filter { it.path != path }.sumOf { it.size }
                )
            } catch (_: Exception) { }
        }
    }

    fun deleteFiles(paths: List<String>) {
        viewModelScope.launch {
            paths.forEach { path ->
                try {
                    val file = File(path)
                    if (file.exists()) {
                        if (file.isDirectory) file.deleteRecursively() else file.delete()
                    }
                } catch (_: Exception) { }
            }
            val pathSet = paths.toSet()
            _state.value = _state.value.copy(
                junkFiles = _state.value.junkFiles.filter { it.path !in pathSet },
                largeFiles = _state.value.largeFiles.filter { it.path !in pathSet },
                emptyFolders = _state.value.emptyFolders.filter { it.path !in pathSet },
                oldApks = _state.value.oldApks.filter { it.path !in pathSet },
                disguisedFiles = _state.value.disguisedFiles.filter { it.path !in pathSet },
                totalJunkSize = _state.value.junkFiles.filter { it.path !in pathSet }.sumOf { it.size }
            )
        }
    }
}
