package com.phantomfiles.pro.presentation.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.local.BookmarkDao
import com.phantomfiles.pro.data.local.OperationLogDao
import com.phantomfiles.pro.data.model.Bookmark
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.data.model.OperationLog
import com.phantomfiles.pro.data.repository.FileRepository
import com.phantomfiles.pro.data.repository.RecycleBinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class FilesUiState {
    data object Loading : FilesUiState()
    data class Success(val files: List<FileItem>) : FilesUiState()
    data class Error(val message: String) : FilesUiState()
}

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val recycleBinRepository: RecycleBinRepository,
    private val bookmarkDao: BookmarkDao,
    private val operationLogDao: OperationLogDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<FilesUiState>(FilesUiState.Loading)
    val uiState: StateFlow<FilesUiState> = _uiState

    private val _pathStack = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val pathStack: StateFlow<List<Pair<String, String>>> = _pathStack

    private val _currentPath = MutableStateFlow("")
    val currentPath: StateFlow<String> = _currentPath

    private val _selectedFiles = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles: StateFlow<Set<String>> = _selectedFiles

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _sortMode = MutableStateFlow("name_asc")
    val sortMode: StateFlow<String> = _sortMode

    private val _viewMode = MutableStateFlow("list")
    val viewMode: StateFlow<String> = _viewMode

    private val _showHidden = MutableStateFlow(false)
    val showHidden: StateFlow<Boolean> = _showHidden

    private val _clipboard = MutableStateFlow<Pair<ClipboardOp, List<String>>?>(null)
    val clipboard: Pair<ClipboardOp, List<String>>? get() = _clipboard.value

    private val _operationProgress = MutableStateFlow<Pair<String, Int>?>(null)
    val operationProgress: StateFlow<Pair<String, Int>?> = _operationProgress

    enum class ClipboardOp { COPY, MOVE }

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        val root = fileRepository.getRootPath()
        _pathStack.value = listOf("Internal Storage" to root)
        _currentPath.value = root
        loadFiles()
    }

    fun loadFiles() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = FilesUiState.Loading
            fileRepository.listFiles(_currentPath.value, _showHidden.value)
                .catch { _uiState.value = FilesUiState.Error(it.message ?: "Failed to load files") }
                .collect { files ->
                    _uiState.value = FilesUiState.Success(sortFiles(files))
                }
        }
    }

    fun initializeAtPath(path: String, name: String) {
        _pathStack.value = listOf(name to path)
        _currentPath.value = path
        _selectedFiles.value = emptySet()
        loadFiles()
    }

    fun navigateToPath(path: String, name: String) {
        val stack = _pathStack.value.toMutableList()
        stack.add(name to path)
        _pathStack.value = stack
        _currentPath.value = path
        _selectedFiles.value = emptySet()
        loadFiles()
    }

    fun navigateToFolder(fileItem: FileItem) {
        if (!fileItem.isDirectory) return
        navigateToPath(fileItem.path, fileItem.name)
    }

    fun navigateBack(): Boolean {
        val stack = _pathStack.value
        if (stack.size <= 1) return false
        val newStack = stack.dropLast(1)
        _pathStack.value = newStack
        _currentPath.value = newStack.last().second
        _selectedFiles.value = emptySet()
        loadFiles()
        return true
    }

    fun navigateToBreadcrumb(index: Int) {
        val stack = _pathStack.value
        if (index >= stack.size - 1) return
        val newStack = stack.take(index + 1)
        _pathStack.value = newStack
        _currentPath.value = newStack.last().second
        _selectedFiles.value = emptySet()
        loadFiles()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            loadFiles()
        } else {
            searchJob = viewModelScope.launch {
                delay(300)
                _uiState.value = FilesUiState.Loading
                fileRepository.searchFiles(_currentPath.value, query)
                    .catch { _uiState.value = FilesUiState.Error(it.message ?: "Search failed") }
                    .collect { files -> _uiState.value = FilesUiState.Success(sortFiles(files)) }
            }
        }
    }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) {
            _searchQuery.value = ""
            loadFiles()
        }
    }

    fun setSortMode(mode: String) {
        _sortMode.value = mode
        val current = _uiState.value
        if (current is FilesUiState.Success) {
            _uiState.value = FilesUiState.Success(sortFiles(current.files))
        }
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == "list") "grid" else "list"
    }

    fun toggleHiddenFiles() {
        _showHidden.value = !_showHidden.value
        loadFiles()
    }

    fun toggleFileSelection(path: String) {
        val current = _selectedFiles.value.toMutableSet()
        if (current.contains(path)) current.remove(path) else current.add(path)
        _selectedFiles.value = current
    }

    fun selectAll() {
        val current = _uiState.value
        if (current is FilesUiState.Success) {
            _selectedFiles.value = current.files.map { it.path }.toSet()
        }
    }

    fun clearSelection() {
        _selectedFiles.value = emptySet()
    }

    fun copySelected() {
        _clipboard.value = ClipboardOp.COPY to _selectedFiles.value.toList()
        clearSelection()
    }

    fun cutSelected() {
        _clipboard.value = ClipboardOp.MOVE to _selectedFiles.value.toList()
        clearSelection()
    }

    fun paste() {
        val clip = _clipboard.value ?: return
        viewModelScope.launch {
            val destDir = _currentPath.value
            clip.second.forEachIndexed { index, path ->
                _operationProgress.value = "Pasting..." to ((index + 1) * 100 / clip.second.size)
                when (clip.first) {
                    ClipboardOp.COPY -> fileRepository.copyFile(path, destDir)
                    ClipboardOp.MOVE -> fileRepository.moveFile(path, destDir)
                }
                operationLogDao.insert(OperationLog(operation = clip.first.name, sourcePath = path, destPath = destDir))
            }
            _clipboard.value = null
            _operationProgress.value = null
            loadFiles()
        }
    }

    fun deleteSelected(permanent: Boolean = false) {
        viewModelScope.launch {
            val paths = _selectedFiles.value.toList()
            paths.forEachIndexed { index, path ->
                _operationProgress.value = "Deleting..." to ((index + 1) * 100 / paths.size)
                if (permanent) {
                    fileRepository.deleteFile(path)
                } else {
                    recycleBinRepository.moveToRecycleBin(path)
                }
                operationLogDao.insert(OperationLog(operation = "DELETE", sourcePath = path))
            }
            _selectedFiles.value = emptySet()
            _operationProgress.value = null
            loadFiles()
        }
    }

    fun renameFile(path: String, newName: String) {
        viewModelScope.launch {
            fileRepository.renameFile(path, newName)
            operationLogDao.insert(OperationLog(operation = "RENAME", sourcePath = path, destPath = newName))
            loadFiles()
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            fileRepository.createFolder(_currentPath.value, name)
            operationLogDao.insert(OperationLog(operation = "CREATE_FOLDER", sourcePath = "${_currentPath.value}/$name"))
            loadFiles()
        }
    }

    fun createFile(name: String) {
        viewModelScope.launch {
            fileRepository.createFile(_currentPath.value, name)
            loadFiles()
        }
    }

    fun compressSelected() {
        viewModelScope.launch {
            val paths = _selectedFiles.value.toList()
            if (paths.isEmpty()) return@launch
            val firstName = File(paths.first()).name
            val zipName = if (paths.size == 1) "${firstName}.zip" else "archive_${System.currentTimeMillis()}.zip"
            val destPath = "${_currentPath.value}/$zipName"
            _operationProgress.value = "Compressing..." to 0
            fileRepository.compressToZip(paths, destPath) { progress ->
                _operationProgress.value = "Compressing..." to progress
            }
            _selectedFiles.value = emptySet()
            _operationProgress.value = null
            loadFiles()
        }
    }

    fun extractZip(path: String) {
        viewModelScope.launch {
            _operationProgress.value = "Extracting..." to 0
            fileRepository.extractZip(path, _currentPath.value) { count ->
                _operationProgress.value = "Extracting... ($count files)" to 50
            }
            _operationProgress.value = null
            loadFiles()
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val path = _currentPath.value
            val name = _pathStack.value.lastOrNull()?.first ?: "Bookmark"
            if (bookmarkDao.isBookmarked(path)) {
                bookmarkDao.getAllBookmarks().first().find { it.path == path }?.let {
                    bookmarkDao.delete(it)
                }
            } else {
                bookmarkDao.insert(Bookmark(path = path, name = name))
            }
        }
    }

    private fun sortFiles(files: List<FileItem>): List<FileItem> {
        val dirs = files.filter { it.isDirectory }
        val regular = files.filter { !it.isDirectory }
        val sortedDirs = when (_sortMode.value) {
            "name_desc" -> dirs.sortedByDescending { it.name.lowercase() }
            "size_asc" -> dirs.sortedBy { it.size }
            "size_desc" -> dirs.sortedByDescending { it.size }
            "date_asc" -> dirs.sortedBy { it.lastModified }
            "date_desc" -> dirs.sortedByDescending { it.lastModified }
            "type" -> dirs.sortedBy { it.extension.lowercase() }
            else -> dirs.sortedBy { it.name.lowercase() }
        }
        val sortedFiles = when (_sortMode.value) {
            "name_desc" -> regular.sortedByDescending { it.name.lowercase() }
            "size_asc" -> regular.sortedBy { it.size }
            "size_desc" -> regular.sortedByDescending { it.size }
            "date_asc" -> regular.sortedBy { it.lastModified }
            "date_desc" -> regular.sortedByDescending { it.lastModified }
            "type" -> regular.sortedBy { it.extension.lowercase() }
            else -> regular.sortedBy { it.name.lowercase() }
        }
        return sortedDirs + sortedFiles
    }
}
