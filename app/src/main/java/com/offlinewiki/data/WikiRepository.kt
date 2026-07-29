package com.offlinewiki.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.UUID

class WikiRepository(
    private val db: AppDatabase,
    private val context: Context,
    private val storagePrefs: StoragePreferences = StoragePreferences(context)
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = OkHttpClient()

    // Storage path helper
    private fun getImageStorageDir(): java.io.File {
        val sdPath = storagePrefs.sdPath
        return if (sdPath != null) {
            java.io.File(sdPath).apply { if (!exists()) mkdirs() }
        } else {
            context.getDir("wiki_images", Context.MODE_PRIVATE)
        }
    }

    // Articles
    fun getAllArticles(): Flow<List<Article>> = db.articleDao().getAllArticles()
    fun getArticlesByFolder(folderId: String): Flow<List<Article>> = db.articleDao().getArticlesByFolder(folderId)
    suspend fun getArticle(id: String): Article? = db.articleDao().getById(id)
    suspend fun saveArticle(article: Article) = db.articleDao().insert(article)
    suspend fun deleteArticle(article: Article) {
        db.imageDao().deleteByArticle(article.articleId)
        db.articleDao().delete(article)
    }
    fun search(query: String): Flow<List<Article>> = db.articleDao().searchArticles(query)

    // Folders
    fun getAllFolders(): Flow<List<Folder>> = db.folderDao().getAllFolders()
    suspend fun createFolder(name: String, parentId: String? = null) {
        val folder = Folder(folderId = UUID.randomUUID().toString(), name = name, parentFolderId = parentId)
        db.folderDao().insert(folder)
    }
    suspend fun deleteFolder(folder: Folder) = db.folderDao().delete(folder)

    // Images
    suspend fun getImages(articleId: String): List<ImageCache> = db.imageDao().getImagesForArticle(articleId)
    suspend fun cacheImage(url: String, articleId: String, localPath: String, width: Int = 0, height: Int = 0) {
        db.imageDao().insert(ImageCache(url, articleId, localPath, width, height))
    }
    suspend fun getImageByUrl(url: String): ImageCache? = db.imageDao().getByUrl(url)

    // Links
    suspend fun getLinks(articleId: String): List<LinkGraph> = db.linkDao().getLinksFrom(articleId)
    suspend fun saveLink(link: LinkGraph) = db.linkDao().insert(link)
    suspend fun markLinkDownloaded(url: String) = db.linkDao().markDownloaded(url)

    // Download tasks / batch queue
    fun getTasks(): Flow<List<DownloadTask>> = db.downloadDao().getAllTasks()
    suspend fun addDownloadTask(task: DownloadTask) = db.downloadDao().insert(task)
    suspend fun updateTaskStatus(url: String, status: Int, completedAt: Long?) = db.downloadDao().updateStatus(url, status, completedAt)

    // Wikipedia fetch & batch processing
    suspend fun fetchWikipediaPage(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) response.body?.string() else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun processBatchQueue() {
        scope.launch {
            val pending = db.downloadDao().getPendingBatch()
            for (task in pending) {
                try {
                    db.downloadDao().updateStatus(task.url, DownloadTask.STATUS_DOWNLOADING, null)
                    // Download HTML page
                    val html = fetchWikipediaPage(task.url)
                    // Extract article title and markdown
                    val title = extractTitle(html ?: "")
                    val markdown = htmlToMarkdown(html ?: "")
                    val article = Article(
                        articleId = task.url.hashCode().toString() + UUID.randomUUID().toString(),
                        title = title,
                        url = task.url,
                        markdownContent = markdown,
                        folderId = task.folderId
                    )
                    db.articleDao().insert(article)
                    db.downloadDao().updateStatus(task.url, DownloadTask.STATUS_COMPLETED, System.currentTimeMillis())
                    // Save links for recursive download
                    val links = extractLinks(html ?: "", article.articleId)
                    links.forEach { db.linkDao().insert(it) }
                    // Add nested links as tasks if depth < 2
                    if (task.depth < 2) {
                        links.take(10).forEach { link ->
                            if (db.downloadDao().pendingCount() < 50) {
                                db.downloadDao().insert(
                                    DownloadTask(
                                        url = link.targetUrl,
                                        articleTitle = link.linkText,
                                        folderId = task.folderId,
                                        depth = task.depth + 1
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    db.downloadDao().updateStatus(task.url, DownloadTask.STATUS_FAILED, null)
                }
            }
        }
    }

    private fun extractTitle(html: String): String {
        val titleRegex = "<title>(.*?)</title>".toRegex()
        return titleRegex.find(html)?.groupValues?.get(1) ?: "Untitled"
    }

    private fun htmlToMarkdown(html: String): String {
        // Simplified HTML to Markdown conversion for demonstration
        val cleaned = html
            .replace("<h1[^>]*>(.*?)</h1>".toRegex(RegexOption.IGNORE_CASE), "# $1\n")
            .replace("<h2[^>]*>(.*?)</h2>".toRegex(RegexOption.IGNORE_CASE), "## $1\n")
            .replace("<h3[^>]*>(.*?)</h3>".toRegex(RegexOption.IGNORE_CASE), "### $1\n")
            .replace("<p[^>]*>(.*?)</p>".toRegex(RegexOption.IGNORE_CASE), "$1\n\n")
            .replace("<br\\s*/?>".toRegex(RegexOption.IGNORE_CASE), "\n")
            .replace("<strong[^>]*>(.*?)</strong>".toRegex(RegexOption.IGNORE_CASE), "**$1**")
            .replace("<b[^>]*>(.*?)</b>".toRegex(RegexOption.IGNORE_CASE), "**$1**")
            .replace("<a[^>]*href=\"(.*?)\"[^>]*>(.*?)</a>".toRegex(RegexOption.IGNORE_CASE), "[$2]($1)")
            .replace("<[^>]+>".toRegex(), "")
        return cleaned.trim()
    }

    private fun extractLinks(html: String, sourceId: String): List<LinkGraph> {
        val links = mutableListOf<LinkGraph>()
        val regex = "<a[^>]*href=\"(https?://[^\"]+)\"[^>]*>(.*?)</a>".toRegex()
        regex.findAll(html).forEach { match ->
            val url = match.groupValues[1]
            val text = match.groupValues[2].replace("<.*?>", "")
            if (url.contains("wikipedia.org/wiki/") && !url.contains("Special:") && !url.contains("File:")) {
                links.add(LinkGraph(sourceId, url, text, false, 1))
            }
        }
        return links.distinctBy { it.targetUrl }.take(30)
    }

    suspend fun downloadImage(url: String, articleId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = url.substringAfterLast("/").ifEmpty { UUID.randomUUID().toString() + ".jpg" }
                val localDir = getImageStorageDir()
                val file = java.io.File(localDir, fileName)
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.bytes()?.let { bytes ->
                            file.writeBytes(bytes)
                            val cached = ImageCache(url, articleId, file.absolutePath)
                            db.imageDao().insert(cached)
                            file.absolutePath
                        }
                    } else null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
