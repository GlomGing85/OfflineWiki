package com.offlinewiki

import android.app.Application
import com.offlinewiki.data.AppDatabase

class WikiApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
