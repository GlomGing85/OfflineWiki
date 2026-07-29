package com.offlinewiki.data

import androidx.room.*

@Dao
interface LinkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(link: LinkGraph)

    @Update
    suspend fun update(link: LinkGraph)

    @Query("SELECT * FROM link_graph WHERE sourceArticleId = :articleId")
    suspend fun getLinksFrom(articleId: String): List<LinkGraph>

    @Query("SELECT * FROM link_graph WHERE targetUrl = :url")
    suspend fun getByTargetUrl(url: String): LinkGraph?

    @Query("SELECT * FROM link_graph WHERE isDownloaded = 0 AND depth <= :maxDepth")
    suspend fun getPendingLinks(maxDepth: Int = 2): List<LinkGraph>

    @Query("UPDATE link_graph SET isDownloaded = 1 WHERE targetUrl = :url")
    suspend fun markDownloaded(url: String)
}
