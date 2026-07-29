package com.offlinewiki.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_tasks WHERE status IN (0, 1) ORDER BY addedAt ASC LIMIT 20")
    suspend fun getPendingBatch(): List<DownloadTask>

    @Query("SELECT * FROM download_tasks ORDER BY addedAt DESC")
    fun getAllTasks(): Flow<List<DownloadTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadTask)

    @Update
    suspend fun update(task: DownloadTask)

    @Query("UPDATE download_tasks SET status = :status, completedAt = :completedAt, retryCount = retryCount + 1 WHERE url = :url")
    suspend fun updateStatus(url: String, status: Int, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 0")
    suspend fun pendingCount(): Int

    @Query("DELETE FROM download_tasks WHERE url = :url")
    suspend fun delete(url: String)
}
