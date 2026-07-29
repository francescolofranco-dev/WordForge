package com.wordforge.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wordforge.data.NotificationPreferenceStore

/** Restores the next alarm after a reboot, app update, or wall-clock change. */
class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in SUPPORTED_ACTIONS) return

        NotificationScheduler.ensureScheduled(
            context.applicationContext,
            NotificationPreferenceStore(context).reminderFrequency,
        )
    }

    private companion object {
        val SUPPORTED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
