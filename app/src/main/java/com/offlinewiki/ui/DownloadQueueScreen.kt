package com.offlinewiki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DownloadQueueScreen(
    viewModel: DownloadViewModel,
    onDownloadImage: (String) -> Unit
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Batch Download Queue",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = { viewModel.processBatchQueue() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Process Queue")
        }
        LazyColumn {
            items(tasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (task.status) {
                            com.offlinewiki.data.DownloadTask.STATUS_COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                            com.offlinewiki.data.DownloadTask.STATUS_FAILED -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.articleTitle ?: task.url, style = MaterialTheme.typography.titleMedium)
                        Text("Status: ${when(task.status) { 0 -> "Pending"; 1 -> "Downloading"; 2 -> "Completed"; else -> "Failed" }}", style = MaterialTheme.typography.bodySmall)
                        Text("Depth: ${task.depth}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
