package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.DownloadTask
import com.offlinewiki.data.WikiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DownloadViewModel(private val repository: WikiRepository) : ViewModel() {
    val tasks: StateFlow<List<DownloadTask>> = repository.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun processBatchQueue() {
        viewModelScope.launch { repository.processBatchQueue() }
    }

    fun addTask(url: String, folderId: String? = null, depth: Int = 1) {
        viewModelScope.launch {
            repository.addDownloadTask(
                DownloadTask(url = url, folderId = folderId, depth = depth)
            )
        }
    }
}
