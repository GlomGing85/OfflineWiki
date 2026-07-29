package com.offlinewiki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ArticleListScreen(
    folderId: String? = null,
    viewModel: MainViewModel,
    onNavigateToArticle: (String) -> Unit,
    onNavigateToFolders: () -> Unit
) {
    val articles by viewModel.articles.collectAsState(initial = emptyList())
    val folders by viewModel.folders.collectAsState(initial = emptyList())

    val currentFolder = folders.find { it.folderId == folderId }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = currentFolder?.name ?: "Offline Wiki",
                    style = MaterialTheme.typography.headlineLarge
                )
                if (currentFolder != null) {
                    TextButton(onClick = onNavigateToFolders) {
                        Text("Back to folders", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            TextButton(onClick = onNavigateToFolders) {
                Text("Folders")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
        LazyColumn {
            items(articles.filter { if (folderId == null) it.folderId == null else it.folderId == folderId }) { article ->
                Card(
                    onClick = { onNavigateToArticle(article.articleId) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(article.title, style = MaterialTheme.typography.titleMedium)
                            Text(article.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
