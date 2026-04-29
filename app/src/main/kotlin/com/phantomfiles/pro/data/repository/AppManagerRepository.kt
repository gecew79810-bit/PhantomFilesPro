package com.phantomfiles.pro.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.phantomfiles.pro.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManagerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getInstalledApps(includeSystem: Boolean = false): Flow<List<AppInfo>> = flow {
        val pm = context.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        val apps = packages.filter { pkg ->
            includeSystem || (pkg.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) == 0)
        }.mapNotNull { pkg ->
            try {
                val appInfo = pkg.applicationInfo ?: return@mapNotNull null
                val apkFile = File(appInfo.sourceDir)
                val dataDir = appInfo.dataDir?.let { File(it) }
                val cacheSize = try {
                    File(appInfo.dataDir, "cache").walkTopDown().filter { !it.isDirectory }.sumOf { it.length() }
                } catch (_: Exception) { 0L }

                AppInfo(
                    packageName = pkg.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    apkSize = apkFile.length(),
                    dataSize = dataDir?.walkTopDown()?.filter { !it.isDirectory }?.sumOf { it.length() } ?: 0L,
                    cacheSize = cacheSize,
                    installDate = pkg.firstInstallTime,
                    lastUsed = pkg.lastUpdateTime,
                    versionName = pkg.versionName ?: "Unknown",
                    versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pkg.longVersionCode else pkg.versionCode.toLong(),
                    permissions = pkg.requestedPermissions?.toList() ?: emptyList()
                )
            } catch (_: Exception) { null }
        }
        emit(apps)
    }.flowOn(Dispatchers.IO)

    suspend fun extractApk(packageName: String, destDir: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val apkFile = File(appInfo.sourceDir)
            val destFile = File(destDir, "${packageName}.apk")
            apkFile.copyTo(destFile, overwrite = true)
            destFile.absolutePath
        }
    }

    fun getAppDataPath(packageName: String): String = "/storage/emulated/0/Android/data/$packageName"
}
