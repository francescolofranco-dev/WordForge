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

/** Builds the single summary notification shared by all due-word workers. */
object ReviewNotification {
    const val NOTIFICATION_ID = 999999
    const val EXTRA_OPEN_REVIEW = "openOverdueReview"

    @SuppressLint("MissingPermission")
    fun show(context: Context, overdueCount: Int) {
        if (overdueCount <= 0) {
            cancel(context)
            return
        }

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
        val title = if (overdueCount == 1) "1 item ready" else "$overdueCount items ready"
        val body = if (overdueCount == 1) {
            "A quick review will keep it fresh."
        } else {
            "Review them together in one short session."
        }

        val notification = NotificationCompat.Builder(
            context,
            WordForgeApp.NOTIFICATION_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_stat_wordforge)
            .setContentTitle(title)
            .setContentText(body)
            .setNumber(overdueCount)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
