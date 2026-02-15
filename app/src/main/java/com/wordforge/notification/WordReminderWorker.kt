package com.wordforge.notification

import android.Manifest
import com.wordforge.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordforge.MainActivity
import com.wordforge.WordForgeApp

/**
 * Background worker that fires a notification for a specific word.
 *
 * WorkManager triggers this at the scheduled time. It reads the word ID
 * and word text from the input data, builds a notification, and posts it.
 * Tapping the notification opens MainActivity with the word ID so the
 * Quiz screen can load.
 */
class WordReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_WORD_ID = "word_id"
        const val KEY_WORD_TEXT = "word_text"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val wordId = inputData.getString(KEY_WORD_ID) ?: return Result.failure()
        val wordText = inputData.getString(KEY_WORD_TEXT) ?: return Result.failure()

        // Check notification permission (required on Android 13+)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        // Build an intent that opens the quiz for this word
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("wordId", wordId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            wordId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            WordForgeApp.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_stat_wordforge)
            .setContentTitle("Time to review!")
            .setContentText("Do you remember what \"$wordText\" means?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(wordId.hashCode(), notification)

        return Result.success()
    }
}