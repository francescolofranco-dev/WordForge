package com.wordforge.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordforge.data.WordDatabase

/** Refreshes one shared review notification when any scheduled word becomes due. */
class WordReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_WORD_ID = "word_id"
    }

    override suspend fun doWork(): Result {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val dao = WordDatabase.getDatabase(applicationContext).wordDao()
        val overdueCount = dao.getAllForNextPrompting(System.currentTimeMillis()).size
        ReviewNotification.show(applicationContext, overdueCount)
        return Result.success()
    }
}
