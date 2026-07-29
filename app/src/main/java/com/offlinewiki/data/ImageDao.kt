package com.offlinewiki.data

import androidx.room.*

@Dao
interface ImageDao {
    @Query("SELECT * FROM image_cache WHERE articleId = :articleId")
    suspend fun getImagesForArticle(articleId: String): List<ImageCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageCache)

    @Delete
    suspend fun delete(image: ImageCache)

    @Query("SELECT * FROM image_cache WHERE imageUrl = :url")
    suspend fun getByUrl(url: String): ImageCache?

    @Query("DELETE FROM image_cache WHERE articleId = :articleId")
    suspend fun deleteByArticle(articleId: String)
}
