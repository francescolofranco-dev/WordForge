package com.wordforge.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.wordforge.data.NotificationPreferenceStore
import com.wordforge.data.WordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Wakes for one configured slot, schedules the next slot, and posts a single
 * notification containing every item that is overdue at that moment.
 */
class BatchReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_BATCH_REMINDER) return

        val applicationContext = context.applicationContext
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val preferences = NotificationPreferenceStore(applicationContext)

                // Schedule first so a process death while reading the database
                // cannot break the reminder chain.
                NotificationScheduler.ensureScheduled(
                    applicationContext,
                    preferences.reminderFrequency,
                )

                if (hasNotificationPermission(applicationContext)) {
                    val overdueItems = WordDatabase
                        .getDatabase(applicationContext)
                        .wordDao()
                        .getAllForNextPrompting(System.currentTimeMillis())
                    ReviewNotification.show(applicationContext, overdueItems)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val ACTION_SHOW_BATCH_REMINDER =
            "com.wordforge.action.SHOW_BATCH_REMINDER"
    }
}
