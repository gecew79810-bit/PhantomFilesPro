package com.phantomfiles.pro.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.repository.SettingsRepository
import com.phantomfiles.pro.data.repository.ShizukuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val showHiddenFiles: Boolean = false,
    val viewMode: String = "list",
    val sortMode: String = "name_asc",
    val biometricEnabled: Boolean = false,
    val recycleBinDays: Int = 30,
    val recycleBinSizeMb: Int = 500,
    val autoCleanCache: Boolean = false,
    val groqApiKey: String = "",
    val geminiApiKey: String = "",
    val ftpPort: Int = 2121,
    val shizukuAvailable: Boolean = false,
    val shizukuConnected: Boolean = false,
    val isRooted: Boolean = false,
    val ftpPassword: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val shizukuRepository: ShizukuRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.showHiddenFiles,
                settingsRepository.viewMode,
                settingsRepository.biometricEnabled,
                settingsRepository.recycleBinDays,
                settingsRepository.groqApiKey
            ) { hidden, viewMode, biometric, rbDays, groqKey ->
                _state.value.copy(
                    showHiddenFiles = hidden,
                    viewMode = viewMode,
                    biometricEnabled = biometric,
                    recycleBinDays = rbDays,
                    groqApiKey = groqKey,
                    shizukuAvailable = shizukuRepository.isAvailable.value,
                    shizukuConnected = shizukuRepository.isConnected.value,
                    isRooted = com.phantomfiles.pro.util.PermissionUtils.isRooted()
                )
            }.collect { _state.value = it }
        }
        viewModelScope.launch {
            combine(
                settingsRepository.ftpPassword,
                settingsRepository.autoCleanCache,
                settingsRepository.geminiApiKey
            ) { ftpPwd, autoClean, geminiKey ->
                _state.value.copy(ftpPassword = ftpPwd, autoCleanCache = autoClean, geminiApiKey = geminiKey)
            }.collect { _state.value = it }
        }
    }

    fun setShowHiddenFiles(show: Boolean) { viewModelScope.launch { settingsRepository.setShowHiddenFiles(show) } }
    fun setViewMode(mode: String) { viewModelScope.launch { settingsRepository.setViewMode(mode) } }
    fun setBiometricEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepository.setBiometricEnabled(enabled) } }
    fun setRecycleBinDays(days: Int) { viewModelScope.launch { settingsRepository.setRecycleBinDays(days) } }
    fun setGroqApiKey(key: String) { viewModelScope.launch { settingsRepository.setGroqApiKey(key) } }
    fun setAutoCleanCache(enabled: Boolean) { viewModelScope.launch { settingsRepository.setAutoCleanCache(enabled) } }

    fun setFtpPassword(password: String) { viewModelScope.launch { settingsRepository.setFtpPassword(password) } }
    fun setGeminiApiKey(key: String) { viewModelScope.launch { settingsRepository.setGeminiApiKey(key) } }

    fun requestShizukuPermission() { shizukuRepository.requestPermission() }
}
