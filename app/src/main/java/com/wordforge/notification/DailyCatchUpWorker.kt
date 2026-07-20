package com.wordforge.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordforge.data.WordDatabase

/** Refreshes the same due-count summary each morning for reviews the user missed. */
class DailyCatchUpWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_catchup"
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
