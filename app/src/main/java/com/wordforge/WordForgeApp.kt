package com.wordforge

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.wordforge.notification.NotificationScheduler

class WordForgeApp : Application() {

    companion object {
        // A new channel id lets existing installs receive the quieter summary defaults;
        // Android does not allow an app to lower an already-created channel's importance.
        const val NOTIFICATION_CHANNEL_ID = "wordforge_review_summaries"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        NotificationScheduler.scheduleDailyCatchUp(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Review reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "A summary when learning items are ready to review"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
