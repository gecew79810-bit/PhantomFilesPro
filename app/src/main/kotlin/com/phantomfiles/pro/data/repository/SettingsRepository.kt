package com.phantomfiles.pro.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "phantom_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val SHOW_HIDDEN_FILES = booleanPreferencesKey("show_hidden_files")
        val VIEW_MODE = stringPreferencesKey("view_mode")
        val SORT_MODE = stringPreferencesKey("sort_mode")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val VAULT_PIN = stringPreferencesKey("vault_pin")
        val RECYCLE_BIN_DAYS = intPreferencesKey("recycle_bin_days")
        val RECYCLE_BIN_SIZE_MB = intPreferencesKey("recycle_bin_size_mb")
        val AUTO_CLEAN_CACHE = booleanPreferencesKey("auto_clean_cache")
        val GROQ_API_KEY = stringPreferencesKey("groq_api_key")
        val FTP_PASSWORD = stringPreferencesKey("ftp_password")
        val FTP_PORT = intPreferencesKey("ftp_port")
    }

    val showHiddenFiles: Flow<Boolean> = context.dataStore.data.map { it[SHOW_HIDDEN_FILES] ?: false }
    val viewMode: Flow<String> = context.dataStore.data.map { it[VIEW_MODE] ?: "list" }
    val sortMode: Flow<String> = context.dataStore.data.map { it[SORT_MODE] ?: "name_asc" }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    val vaultPin: Flow<String> = context.dataStore.data.map { it[VAULT_PIN] ?: "" }
    val recycleBinDays: Flow<Int> = context.dataStore.data.map { it[RECYCLE_BIN_DAYS] ?: 30 }
    val recycleBinSizeMb: Flow<Int> = context.dataStore.data.map { it[RECYCLE_BIN_SIZE_MB] ?: 500 }
    val autoCleanCache: Flow<Boolean> = context.dataStore.data.map { it[AUTO_CLEAN_CACHE] ?: false }
    val groqApiKey: Flow<String> = context.dataStore.data.map { it[GROQ_API_KEY] ?: "" }
    val ftpPassword: Flow<String> = context.dataStore.data.map { it[FTP_PASSWORD] ?: "" }
    val ftpPort: Flow<Int> = context.dataStore.data.map { it[FTP_PORT] ?: 2121 }

    suspend fun setShowHiddenFiles(show: Boolean) = context.dataStore.edit { it[SHOW_HIDDEN_FILES] = show }
    suspend fun setViewMode(mode: String) = context.dataStore.edit { it[VIEW_MODE] = mode }
    suspend fun setSortMode(mode: String) = context.dataStore.edit { it[SORT_MODE] = mode }
    suspend fun setBiometricEnabled(enabled: Boolean) = context.dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    suspend fun setVaultPin(pin: String) = context.dataStore.edit { it[VAULT_PIN] = pin }
    suspend fun setRecycleBinDays(days: Int) = context.dataStore.edit { it[RECYCLE_BIN_DAYS] = days }
    suspend fun setRecycleBinSizeMb(mb: Int) = context.dataStore.edit { it[RECYCLE_BIN_SIZE_MB] = mb }
    suspend fun setAutoCleanCache(enabled: Boolean) = context.dataStore.edit { it[AUTO_CLEAN_CACHE] = enabled }
    suspend fun setGroqApiKey(key: String) = context.dataStore.edit { it[GROQ_API_KEY] = key }
    suspend fun setFtpPassword(password: String) = context.dataStore.edit { it[FTP_PASSWORD] = password }
    suspend fun setFtpPort(port: Int) = context.dataStore.edit { it[FTP_PORT] = port }
}
