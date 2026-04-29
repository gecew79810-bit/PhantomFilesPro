package com.phantomfiles.pro.data.repository

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.phantomfiles.pro.data.model.FileItem
import com.phantomfiles.pro.shizuku.IFileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuRepository @Inject constructor() {

    private var fileService: IFileService? = null
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        _isAvailable.value = true
        checkPermission()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _isAvailable.value = false
        _isConnected.value = false
        fileService = null
    }

    private val permissionResultListener = Shizuku.OnRequestPermissionResultListener { _, grantResult ->
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            bindService()
        }
    }

    fun initialize() {
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        Shizuku.addRequestPermissionResultListener(permissionResultListener)
    }

    fun cleanup() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(permissionResultListener)
    }

    fun checkPermission(): Boolean {
        return try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                bindService()
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun requestPermission(requestCode: Int = 1000) {
        try {
            Shizuku.requestPermission(requestCode)
        } catch (_: Exception) { }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            fileService = IFileService.Stub.asInterface(service)
            _isConnected.value = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            fileService = null
            _isConnected.value = false
        }
    }

    private fun bindService() {
        try {
            val userServiceArgs = Shizuku.UserServiceArgs(
                ComponentName(
                    "com.phantomfiles.pro",
                    "com.phantomfiles.pro.shizuku.FileServiceImpl"
                )
            ).daemon(false).processNameSuffix("file_service")

            Shizuku.bindUserService(userServiceArgs, serviceConnection)
        } catch (_: Exception) { }
    }

    fun listFiles(path: String): Flow<List<FileItem>> = flow {
        val service = fileService
        if (service != null) {
            val files = service.listFiles(path)
            emit(files.mapNotNull { filePath ->
                try {
                    val file = File(filePath)
                    FileItem(
                        path = file.absolutePath,
                        name = file.name,
                        size = if (file.isDirectory) 0 else service.getFileSize(filePath),
                        lastModified = file.lastModified(),
                        mimeType = java.net.URLConnection.guessContentTypeFromName(file.name),
                        isDirectory = file.isDirectory,
                        isHidden = file.isHidden
                    )
                } catch (_: Exception) { null }
            })
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun copyFile(source: String, dest: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val service = fileService ?: throw IllegalStateException("Shizuku service not connected")
            if (!service.copyFile(source, dest)) throw java.io.IOException("Copy failed via Shizuku")
        }
    }

    suspend fun deleteFile(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val service = fileService ?: throw IllegalStateException("Shizuku service not connected")
            if (!service.deleteFile(path)) throw java.io.IOException("Delete failed via Shizuku")
        }
    }

    fun isShizukuInstalled(): Boolean = try {
        Shizuku.pingBinder()
        true
    } catch (_: Exception) {
        false
    }
}
