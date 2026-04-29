package com.phantomfiles.pro.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phantomfiles.pro.data.repository.ScanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class CacheCleanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scanRepository: ScanRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val junkFiles = scanRepository.findJunkFiles("/storage/emulated/0").first()
            var cleaned = 0L
            junkFiles.forEach { file ->
                try {
                    val f = java.io.File(file.path)
                    if (f.exists()) {
                        cleaned += f.length()
                        if (f.isDirectory) f.deleteRecursively() else f.delete()
                    }
                } catch (_: Exception) { }
            }
            scanRepository.saveScanResult("cache_clean", junkFiles.size, cleaned)
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
