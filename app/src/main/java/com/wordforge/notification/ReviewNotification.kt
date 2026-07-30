package com.wordforge.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wordforge.MainActivity
import com.wordforge.R
import com.wordforge.WordForgeApp
import com.wordforge.data.Word

/** Builds the single summary notification shared by all due-word workers. */
object ReviewNotification {
    const val NOTIFICATION_ID = 999999
    const val EXTRA_OPEN_REVIEW = "openOverdueReview"

    @SuppressLint("MissingPermission")
    fun show(context: Context, overdueItems: List<Word>) {
        if (overdueItems.isEmpty()) {
            cancel(context)
            return
        }
        val overdueCount = overdueItems.size
        val copy = reviewNotificationCopy(overdueCount)

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(EXTRA_OPEN_REVIEW, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(
            context,
            WordForgeApp.NOTIFICATION_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_stat_wordforge)
            .setContentTitle(copy.title)
            .setContentText(copy.body)
            .setNumber(overdueCount)
            .setOnlyAlertOnce(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        // Replace the prior summary as a fresh event so every configured slot
        // can alert instead of silently updating an existing notification.
        manager.cancel(NOTIFICATION_ID)
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
