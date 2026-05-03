package com.phantomfiles.pro.presentation.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phantomfiles.pro.data.model.VaultFile
import com.phantomfiles.pro.data.repository.SettingsRepository
import com.phantomfiles.pro.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VaultUiState {
    data object Locked : VaultUiState()
    data object Loading : VaultUiState()
    data class Unlocked(val files: List<VaultFile>, val totalSize: Long) : VaultUiState()
    data class Error(val message: String) : VaultUiState()
}

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VaultUiState>(VaultUiState.Locked)
    val uiState: StateFlow<VaultUiState> = _uiState

    private var vaultPassword: String = ""

    fun unlock(pin: String) {
        viewModelScope.launch {
            val savedHash = settingsRepository.vaultPinHash.first()
            if (savedHash.isEmpty()) {
                val legacyPin = settingsRepository.vaultPinLegacy.first()
                if (legacyPin.isNotEmpty()) {
                    if (pin == legacyPin) {
                        settingsRepository.setVaultPin(pin)
                        vaultPassword = pin
                        loadFiles()
                    } else {
                        _uiState.value = VaultUiState.Error("Wrong PIN")
                    }
                } else {
                    settingsRepository.setVaultPin(pin)
                    vaultPassword = pin
                    loadFiles()
                }
            } else {
                val saltHex = settingsRepository.vaultPinSalt.first()
                val salt = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val inputHash = SettingsRepository.hashPin(pin, salt)
                if (inputHash == savedHash) {
                    vaultPassword = pin
                    loadFiles()
                } else {
                    _uiState.value = VaultUiState.Error("Wrong PIN")
                }
            }
        }
    }

    private fun loadFiles() {
        viewModelScope.launch {
            _uiState.value = VaultUiState.Loading
            vaultRepository.getAllFiles().collect { files ->
                val totalSize = vaultRepository.getVaultSize()
                _uiState.value = VaultUiState.Unlocked(files, totalSize)
            }
        }
    }

    fun importFile(filePath: String) {
        viewModelScope.launch {
            vaultRepository.importToVault(filePath, vaultPassword)
        }
    }

    fun exportFile(vaultFile: VaultFile, destDir: String) {
        viewModelScope.launch {
            vaultRepository.exportFromVault(vaultFile, destDir, vaultPassword)
        }
    }

    fun removeFile(vaultFile: VaultFile) {
        viewModelScope.launch {
            vaultRepository.removeFromVault(vaultFile)
        }
    }

    fun unlockViaBiometric() {
        viewModelScope.launch {
            vaultPassword = "biometric_unlock"
            loadFiles()
        }
    }

    fun lock() {
        vaultPassword = ""
        _uiState.value = VaultUiState.Locked
    }
}
