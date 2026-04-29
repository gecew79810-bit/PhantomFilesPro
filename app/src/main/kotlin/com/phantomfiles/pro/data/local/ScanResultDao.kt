package com.phantomfiles.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phantomfiles.pro.data.model.ScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_results ORDER BY scannedAt DESC")
    fun getAllResults(): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: ScanResult): Long

    @Query("SELECT * FROM scan_results WHERE scanType = :type ORDER BY scannedAt DESC LIMIT 1")
    suspend fun getLatestByType(type: String): ScanResult?

    @Query("DELETE FROM scan_results")
    suspend fun clearAll()
}
