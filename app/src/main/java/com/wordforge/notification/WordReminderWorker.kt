package com.wordforge.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Compatibility shell for per-item work persisted by older app versions.
 * It intentionally expires without notifying.
 */
class WordReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_WORD_ID = "word_id"
    }

    override suspend fun doWork(): Result = Result.success()
}
