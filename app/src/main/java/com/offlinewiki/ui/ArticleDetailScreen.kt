package com.offlinewiki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ArticleDetailScreen(
    articleId: String,
    viewModel: ArticleDetailViewModel,
    onBack: () -> Unit,
    onDownloadImages: (String) -> Unit
) {
    val article by viewModel.article.collectAsState(initial = null)
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(article?.title ?: "Loading...") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { article?.articleId?.let { onDownloadImages(it) } }) {
                    Icon(Icons.Default.Image, contentDescription = "Download images")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Divider()
        article?.let { art ->
            MarkdownRenderer(
                markdown = art.markdownContent,
                modifier = Modifier.weight(1f).padding(16.dp)
            )
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Article Info", style = MaterialTheme.typography.titleMedium)
                    Text("URL: ${art.url}", style = MaterialTheme.typography.bodySmall)
                    Text("Saved: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(art.downloadedAt))}", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { viewModel.deleteArticle(art); onBack() }) {
                        Text("Delete Article", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
