package com.phantomfiles.pro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phantomfiles.pro.data.model.VaultFile
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_files ORDER BY addedAt DESC")
    fun getAllFiles(): Flow<List<VaultFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: VaultFile): Long

    @Delete
    suspend fun delete(file: VaultFile)

    @Query("SELECT COUNT(*) FROM vault_files")
    suspend fun getFileCount(): Int

    @Query("SELECT SUM(fileSize) FROM vault_files")
    suspend fun getTotalSize(): Long?
}
