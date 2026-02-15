package com.wordforge

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.wordforge.notification.NotificationScheduler

class WordForgeApp : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "wordforge_reminders"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        NotificationScheduler.scheduleDailyCatchUp(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Word Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to review words you're learning"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}