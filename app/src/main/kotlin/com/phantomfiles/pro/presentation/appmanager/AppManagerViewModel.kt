package com.phantomfiles.pro.presentation.appmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.AppInfo
import com.phantomfiles.pro.data.repository.AppManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppManagerUiState {
    data object Loading : AppManagerUiState()
    data class Success(val apps: List<AppInfo>) : AppManagerUiState()
    data class Error(val message: String) : AppManagerUiState()
}

@HiltViewModel
class AppManagerViewModel @Inject constructor(
    private val appManagerRepository: AppManagerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppManagerUiState>(AppManagerUiState.Loading)
    val uiState: StateFlow<AppManagerUiState> = _uiState

    private val _sortMode = MutableStateFlow("name")
    val sortMode: StateFlow<String> = _sortMode

    private val _showSystem = MutableStateFlow(false)
    val showSystem: StateFlow<Boolean> = _showSystem

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = AppManagerUiState.Loading
            try {
                val apps = appManagerRepository.getInstalledApps(_showSystem.value).first()
                _uiState.value = AppManagerUiState.Success(sortApps(apps))
            } catch (e: Exception) {
                _uiState.value = AppManagerUiState.Error(e.message ?: "Failed to load apps")
            }
        }
    }

    fun setSortMode(mode: String) {
        _sortMode.value = mode
        val current = _uiState.value
        if (current is AppManagerUiState.Success) {
            _uiState.value = AppManagerUiState.Success(sortApps(current.apps))
        }
    }

    fun toggleSystemApps() {
        _showSystem.value = !_showSystem.value
        loadApps()
    }

    fun extractApk(packageName: String, destDir: String) {
        viewModelScope.launch {
            appManagerRepository.extractApk(packageName, destDir)
        }
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> = when (_sortMode.value) {
        "size" -> apps.sortedByDescending { it.apkSize + it.dataSize }
        "install_date" -> apps.sortedByDescending { it.installDate }
        "last_used" -> apps.sortedByDescending { it.lastUsed }
        "data_size" -> apps.sortedByDescending { it.dataSize }
        else -> apps.sortedBy { it.appName.lowercase() }
    }
}
