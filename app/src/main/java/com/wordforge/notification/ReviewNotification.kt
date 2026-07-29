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
import com.wordforge.data.LearningItemType
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
            overdueItems.single().notificationLabel()
        } else {
            overdueItems
                .take(3)
                .joinToString(" · ") { it.notificationLabel() }
        }
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(title)
        overdueItems.take(MAX_INBOX_ITEMS).forEach { item ->
            inboxStyle.addLine(item.notificationLabel())
        }
        if (overdueCount > MAX_INBOX_ITEMS) {
            inboxStyle.setSummaryText("+${overdueCount - MAX_INBOX_ITEMS} more")
        } else {
            inboxStyle.setSummaryText("Ready to review")
        }

        val notification = NotificationCompat.Builder(
            context,
            WordForgeApp.NOTIFICATION_CHANNEL_ID,
        )
            .setSmallIcon(R.drawable.ic_stat_wordforge)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(inboxStyle)
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

    private fun Word.notificationLabel(): String =
        if (itemType == LearningItemType.VERB_CONJUGATION) {
            "$word · ${verbConjugation?.tense.orEmpty()}"
        } else {
            word
        }

    private const val MAX_INBOX_ITEMS = 5
}
