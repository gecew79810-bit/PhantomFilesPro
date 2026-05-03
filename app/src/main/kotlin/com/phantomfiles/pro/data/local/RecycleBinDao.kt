package com.phantomfiles.pro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phantomfiles.pro.data.model.RecycleBinItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecycleBinDao {
    @Query("SELECT * FROM recycle_bin ORDER BY deletedAt DESC")
    fun getAllItems(): Flow<List<RecycleBinItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecycleBinItem): Long

    @Delete
    suspend fun delete(item: RecycleBinItem)

    @Query("SELECT * FROM recycle_bin WHERE deletedAt < :beforeTimestamp")
    suspend fun getItemsOlderThan(beforeTimestamp: Long): List<RecycleBinItem>

    @Query("DELETE FROM recycle_bin WHERE deletedAt < :beforeTimestamp")
    suspend fun deleteOlderThan(beforeTimestamp: Long)

    @Query("SELECT SUM(fileSize) FROM recycle_bin")
    suspend fun getTotalSize(): Long?

    @Query("SELECT COUNT(*) FROM recycle_bin")
    suspend fun getItemCount(): Int

    @Query("DELETE FROM recycle_bin")
    suspend fun clearAll()

    @Query("SELECT * FROM recycle_bin WHERE fileName LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<RecycleBinItem>>
}
