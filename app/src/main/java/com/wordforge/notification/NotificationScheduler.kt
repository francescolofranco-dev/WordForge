package com.wordforge.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.work.WorkManager
import com.wordforge.data.ReminderFrequency
import java.time.Instant
import java.time.ZoneId

/**
 * Maintains one inexact, idle-capable alarm for the next grouped reminder.
 *
 * A single alarm avoids a backlog of missed slots firing together after a
 * sleeping device wakes. Each receiver invocation schedules the next future
 * slot before it reads the overdue items.
 */
object NotificationScheduler {

    fun ensureScheduled(
        context: Context,
        frequency: ReminderFrequency,
        nowMillis: Long = System.currentTimeMillis(),
    ) {
        val now = Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault())
        val triggerAtMillis = nextReminderAt(now, frequency).toInstant().toEpochMilli()
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        AlarmManagerCompat.setAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            reminderPendingIntent(context),
        )
    }

    fun reschedule(
        context: Context,
        frequency: ReminderFrequency,
    ) {
        cancelScheduledAlarm(context)
        ReviewNotification.cancel(context)
        ensureScheduled(context, frequency)
    }

    fun cancelDisplayedSummary(context: Context) {
        ReviewNotification.cancel(context)
    }

    /**
     * Stops the old daily/instant WorkManager paths. Already-persisted
     * per-item workers use compatibility no-op classes and expire silently.
     */
    fun cancelLegacySchedules(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(DailyCatchUpWorker.WORK_NAME)
        workManager.cancelUniqueWork(LEGACY_SUMMARY_REFRESH_WORK_NAME)
    }

    private fun cancelScheduledAlarm(context: Context) {
        context.getSystemService(AlarmManager::class.java)
            .cancel(reminderPendingIntent(context))
    }

    private fun reminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, BatchReminderReceiver::class.java).apply {
            action = BatchReminderReceiver.ACTION_SHOW_BATCH_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            BATCH_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private const val BATCH_REMINDER_REQUEST_CODE = 4109
    private const val LEGACY_SUMMARY_REFRESH_WORK_NAME = "review_summary_refresh"
}
