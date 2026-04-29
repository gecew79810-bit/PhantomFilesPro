package com.phantomfiles.pro.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.phantomfiles.pro.data.repository.RecycleBinRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RecycleBinWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val recycleBinRepository: RecycleBinRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            recycleBinRepository.autoClean(30)
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
