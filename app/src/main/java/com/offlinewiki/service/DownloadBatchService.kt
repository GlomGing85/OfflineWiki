package com.offlinewiki.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.offlinewiki.R

class DownloadBatchService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel("download_queue", "Download Queue", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        val notification: Notification = Notification.Builder(this, "download_queue")
            .setContentTitle("Offline Wiki")
            .setContentText("Batch processing articles...")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BatchService", "Starting batch download processing...")
        // The repository's processBatchQueue is triggered from the ViewModel
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BatchService", "Batch service stopped")
    }
}
