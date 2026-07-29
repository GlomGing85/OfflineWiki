package com.offlinewiki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FolderScreen(
    viewModel: FolderViewModel,
    onNavigateToFolder: (String?) -> Unit
) {
    val folders by viewModel.folders.collectAsState(initial = emptyList())
    val showDialog = remember { mutableStateOf(false) }
    val newName = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Folders", style = MaterialTheme.typography.headlineLarge)
            IconButton(onClick = { showDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Folder")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(folders) { folder ->
                Card(
                    onClick = { onNavigateToFolder(folder.folderId) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(folder.name, style = MaterialTheme.typography.titleMedium)
                        }
                        TextButton(onClick = { viewModel.deleteFolder(folder) }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("New Folder") },
            text = {
                OutlinedTextField(
                    value = newName.value,
                    onValueChange = { newName.value = it },
                    label = { Text("Folder name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createFolder(newName.value)
                    showDialog.value = false
                    newName.value = ""
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) { Text("Cancel") }
            }
        )
    }
}
