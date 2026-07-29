package com.offlinewiki.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY lastUpdated DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE folderId = :folderId ORDER BY title ASC")
    fun getArticlesByFolder(folderId: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE articleId = :id")
    suspend fun getById(id: String): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Update
    suspend fun update(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR markdownContent LIKE '%' || :query || '%'")
    fun searchArticles(query: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE folderId IS NULL ORDER BY title ASC")
    fun getUnfoldered(): Flow<List<Article>>
}
