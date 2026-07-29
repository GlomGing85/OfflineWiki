package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.StoragePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: StoragePreferences) : ViewModel() {
    private val _sdPath = MutableStateFlow(prefs.sdPath)
    val sdPath: StateFlow<String?> = _sdPath.asStateFlow()

    private val _downloadImages = MutableStateFlow(prefs.downloadImagesByDefault)
    val downloadImages: StateFlow<Boolean> = _downloadImages.asStateFlow()

    private val _batchSize = MutableStateFlow(prefs.batchSizeLimit)
    val batchSize: StateFlow<Int> = _batchSize.asStateFlow()

    private val _maxDepth = MutableStateFlow(prefs.maxLinkDepth)
    val maxDepth: StateFlow<Int> = _maxDepth.asStateFlow()

    fun setSdPath(path: String?) {
        prefs.sdPath = path
        _sdPath.value = path
    }

    fun setDownloadImages(value: Boolean) {
        prefs.downloadImagesByDefault = value
        _downloadImages.value = value
    }

    fun setBatchSize(value: Int) {
        prefs.batchSizeLimit = value
        _batchSize.value = value
    }

    fun setMaxDepth(value: Int) {
        prefs.maxLinkDepth = value
        _maxDepth.value = value
    }

    fun clearSdPath() {
        prefs.clearSdPath()
        _sdPath.value = null
    }
}
