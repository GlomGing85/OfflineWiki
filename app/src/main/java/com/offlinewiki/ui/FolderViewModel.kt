package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.Folder
import com.offlinewiki.data.WikiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: WikiRepository) : ViewModel() {
    val folders: StateFlow<List<Folder>> = repository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createFolder(name: String, parentId: String? = null) {
        viewModelScope.launch { repository.createFolder(name, parentId) }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch { repository.deleteFolder(folder) }
    }
}
