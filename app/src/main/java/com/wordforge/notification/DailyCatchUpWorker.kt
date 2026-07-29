package com.wordforge.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Compatibility shell for periodic work persisted by older app versions.
 * New grouped reminders are driven by [BatchReminderReceiver].
 */
class DailyCatchUpWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_catchup"
    }

    override suspend fun doWork(): Result = Result.success()
}
