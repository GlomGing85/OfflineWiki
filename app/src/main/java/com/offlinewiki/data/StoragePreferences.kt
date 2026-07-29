package com.offlinewiki.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class StoragePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "offline_wiki_prefs", Context.MODE_PRIVATE
    )

    companion object {
        const val KEY_SD_PATH = "sd_path"
        const val KEY_DOWNLOAD_IMAGES = "download_images_default"
        const val KEY_BATCH_SIZE = "batch_size"
        const val KEY_MAX_DEPTH = "max_depth"
    }

    var sdPath: String?
        get() = prefs.getString(KEY_SD_PATH, null)
        set(value) = prefs.edit().putString(KEY_SD_PATH, value).apply()

    var downloadImagesByDefault: Boolean
        get() = prefs.getBoolean(KEY_DOWNLOAD_IMAGES, true)
        set(value) = prefs.edit().putBoolean(KEY_DOWNLOAD_IMAGES, value).apply()

    var batchSizeLimit: Int
        get() = prefs.getInt(KEY_BATCH_SIZE, 20)
        set(value) = prefs.edit().putInt(KEY_BATCH_SIZE, value).apply()

    var maxLinkDepth: Int
        get() = prefs.getInt(KEY_MAX_DEPTH, 2)
        set(value) = prefs.edit().putInt(KEY_MAX_DEPTH, value).apply()

    fun clearSdPath() {
        prefs.edit().remove(KEY_SD_PATH).apply()
    }
}
