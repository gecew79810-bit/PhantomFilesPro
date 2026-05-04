package com.phantomfiles.pro.presentation.vault

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.phantomfiles.pro.data.model.VaultFile
import com.phantomfiles.pro.data.repository.SettingsRepository
import com.phantomfiles.pro.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<VaultUiState>(VaultUiState.Locked)
    val uiState: StateFlow<VaultUiState> = _uiState

    private var vaultPassword: String = ""

    private val encryptedPrefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "vault_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveVaultPinSecure(pin: String) {
        try {
            encryptedPrefs.edit().putString("vault_pin_secure", pin).apply()
        } catch (_: Exception) { }
    }

    private fun getVaultPinSecure(): String? {
        return try {
            encryptedPrefs.getString("vault_pin_secure", null)
        } catch (_: Exception) {
            null
        }
    }

    fun unlock(pin: String) {
        viewModelScope.launch {
            val savedHash = settingsRepository.vaultPinHash.first()
            if (savedHash.isEmpty()) {
                val legacyPin = settingsRepository.vaultPinLegacy.first()
                if (legacyPin.isNotEmpty()) {
                    if (pin == legacyPin) {
                        settingsRepository.setVaultPin(pin)
                        vaultPassword = pin
                        saveVaultPinSecure(pin)
                        loadFiles()
                    } else {
                        _uiState.value = VaultUiState.Error("Wrong PIN")
                    }
                } else {
                    settingsRepository.setVaultPin(pin)
                    vaultPassword = pin
                    saveVaultPinSecure(pin)
                    loadFiles()
                }
            } else {
                val saltHex = settingsRepository.vaultPinSalt.first()
                val salt = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val inputHash = SettingsRepository.hashPin(pin, salt)
                if (inputHash == savedHash) {
                    vaultPassword = pin
                    saveVaultPinSecure(pin)
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
            val securePin = getVaultPinSecure()
            if (securePin != null) {
                vaultPassword = securePin
                loadFiles()
            } else {
                _uiState.value = VaultUiState.Error("Set up PIN first, then use biometric")
            }
        }
    }

    fun lock() {
        vaultPassword = ""
        _uiState.value = VaultUiState.Locked
    }
}
