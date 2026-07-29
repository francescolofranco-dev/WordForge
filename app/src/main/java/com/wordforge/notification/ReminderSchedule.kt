package com.wordforge.notification

import com.wordforge.data.ReminderFrequency
import java.time.ZonedDateTime

/**
 * Finds the next configured local-time slot strictly after [now].
 */
fun nextReminderAt(
    now: ZonedDateTime,
    frequency: ReminderFrequency,
): ZonedDateTime {
    val today = now.toLocalDate()
    frequency.slotHours.forEach { hour ->
        val candidate = today.atTime(hour, 0).atZone(now.zone)
        if (candidate.isAfter(now)) return candidate
    }

    return today
        .plusDays(1)
        .atTime(frequency.slotHours.first(), 0)
        .atZone(now.zone)
}
