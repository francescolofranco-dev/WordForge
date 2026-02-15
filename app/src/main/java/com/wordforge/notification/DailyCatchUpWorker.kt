package com.wordforge.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordforge.MainActivity
import com.wordforge.R
import com.wordforge.WordForgeApp
import com.wordforge.data.WordDatabase

/**
 * Runs once daily (targeted at 9 AM). Checks for any overdue words
 * and posts a single summary notification if there are any.
 *
 * This catches words whose individual notifications were dismissed
 * or missed, without spamming the user with per-word repeats.
 */
class DailyCatchUpWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val NOTIFICATION_ID = 999999
        const val WORK_NAME = "daily_catchup"
    }

    override suspend fun doWork(): Result {

        android.util.Log.d("WordForge", "DailyCatchUpWorker running")
        // Check notification permission
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        // Query overdue words
        val dao = WordDatabase.getDatabase(applicationContext).wordDao()
        val now = System.currentTimeMillis()
        val overdueWords = dao.getAllForNextPrompting(now)

        android.util.Log.d("WordForge", "Overdue words found: ${overdueWords.size}")

        if (overdueWords.isEmpty()) {
            // Nothing overdue, no notification needed
            return Result.success()
        }

        // Build a summary notification
        val count = overdueWords.size
        val title = if (count == 1) "1 word to review" else "$count words to review"

        // Show up to 3 word previews in the notification body
        val preview = overdueWords
            .take(3)
            .joinToString(", ") { it.word }
        val body = if (count <= 3) preview else "$preview and ${count - 3} more"

        // Tapping opens the app to the word list
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            WordForgeApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_stat_wordforge)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, notification)

        return Result.success()
    }
}