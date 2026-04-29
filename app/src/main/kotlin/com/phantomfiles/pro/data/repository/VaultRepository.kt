package com.phantomfiles.pro.data.repository

import android.content.Context
import com.phantomfiles.pro.data.local.VaultDao
import com.phantomfiles.pro.data.model.VaultFile
import com.phantomfiles.pro.util.AESEncryption
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vaultDao: VaultDao
) {
    private val vaultDir: File
        get() = File(context.filesDir, "vault").also { it.mkdirs() }

    fun getAllFiles(): Flow<List<VaultFile>> = vaultDao.getAllFiles()

    suspend fun importToVault(filePath: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = File(filePath)
            val encryptedName = UUID.randomUUID().toString()
            val dest = File(vaultDir, encryptedName)
            AESEncryption.encryptFile(source, dest, password)
            vaultDao.insert(
                VaultFile(
                    encryptedName = encryptedName,
                    originalName = source.name,
                    originalPath = filePath,
                    fileSize = source.length(),
                    mimeType = java.net.URLConnection.guessContentTypeFromName(source.name)
                )
            )
            source.delete()
            Unit
        }
    }

    suspend fun exportFromVault(vaultFile: VaultFile, destDir: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val encrypted = File(vaultDir, vaultFile.encryptedName)
                val dest = File(destDir, vaultFile.originalName)
                AESEncryption.decryptFile(encrypted, dest, password)
                dest.absolutePath
            }
        }

    suspend fun decryptToTemp(vaultFile: VaultFile, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val encrypted = File(vaultDir, vaultFile.encryptedName)
                val temp = File(context.cacheDir, "vault_preview_${vaultFile.originalName}")
                AESEncryption.decryptFile(encrypted, temp, password)
                temp.absolutePath
            }
        }

    suspend fun removeFromVault(vaultFile: VaultFile): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            File(vaultDir, vaultFile.encryptedName).delete()
            vaultDao.delete(vaultFile)
        }
    }

    suspend fun getVaultSize(): Long = vaultDao.getTotalSize() ?: 0L

    suspend fun getFileCount(): Int = vaultDao.getFileCount()
}
