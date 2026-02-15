package com.wordforge.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Handles scheduling and cancelling word reminder notifications via WorkManager.
 *
 * Two types of notifications:
 * 1. Per-word reminders — one-time, fired at the word's nextPromptAt time
 * 2. Daily catch-up — periodic, runs at 9 AM to remind about any overdue words
 */
object NotificationScheduler {

    private fun workTag(wordId: String) = "word_reminder_$wordId"

    /**
     * Schedules a notification for a word at a specific time.
     * If a notification was already scheduled for this word, it gets replaced.
     */
    fun schedule(context: Context, wordId: String, wordText: String, delayMs: Long) {
        val workManager = WorkManager.getInstance(context)

        // Cancel any existing reminder for this word first
        workManager.cancelAllWorkByTag(workTag(wordId))

        val workRequest = OneTimeWorkRequestBuilder<WordReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    WordReminderWorker.KEY_WORD_ID to wordId,
                    WordReminderWorker.KEY_WORD_TEXT to wordText
                )
            )
            .addTag(workTag(wordId))
            .build()

        workManager.enqueue(workRequest)
    }

    /**
     * Cancels any pending notification for a specific word.
     */
    fun cancel(context: Context, wordId: String) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(workTag(wordId))
    }

    /**
     * Cancels all pending word notifications (but not the daily catch-up).
     */
    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    /**
     * Schedules a daily catch-up worker that runs at approximately 9 AM.
     *
     * Uses PeriodicWorkRequest with a 24-hour interval. The initial delay
     * is calculated to target the next 9 AM from now.
     *
     * KEEP_EXISTING policy means if it's already scheduled, we don't
     * replace it — this is safe to call on every app launch.
     */
    fun scheduleDailyCatchUp(context: Context) {
        val delayMs = msUntilNext9AM()

        val dailyWork = PeriodicWorkRequestBuilder<DailyCatchUpWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyCatchUpWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWork
        )
    }

    /**
     * Calculates milliseconds from now until the next 9:00 AM.
     * If it's currently before 9 AM, targets today.
     * If it's already past 9 AM, targets tomorrow.
     */
    private fun msUntilNext9AM(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If 9 AM has already passed today, target tomorrow
        if (target.before(now) || target == now) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}