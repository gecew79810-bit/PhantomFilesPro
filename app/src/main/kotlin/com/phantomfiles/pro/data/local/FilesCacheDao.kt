package com.phantomfiles.pro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phantomfiles.pro.data.model.FilesCache
import kotlinx.coroutines.flow.Flow

@Dao
interface FilesCacheDao {
    @Query("SELECT * FROM files_cache WHERE path LIKE :parentPath || '/%' AND path NOT LIKE :parentPath || '/%/%'")
    fun getFilesInDirectory(parentPath: String): Flow<List<FilesCache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<FilesCache>)

    @Query("DELETE FROM files_cache WHERE path LIKE :parentPath || '%'")
    suspend fun clearDirectory(parentPath: String)

    @Query("SELECT * FROM files_cache WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<FilesCache>

    @Query("SELECT * FROM files_cache WHERE hash = :hash")
    suspend fun findByHash(hash: String): List<FilesCache>

    @Query("DELETE FROM files_cache")
    suspend fun clearAll()
}
