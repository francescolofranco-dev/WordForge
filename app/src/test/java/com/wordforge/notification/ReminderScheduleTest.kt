package com.wordforge.notification

import com.wordforge.data.ReminderFrequency
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderScheduleTest {
    private val madrid = ZoneId.of("Europe/Madrid")

    @Test
    fun frequenciesExposeOnlyTheSupportedDailyCounts() {
        assertEquals(
            listOf(1, 2, 3, 5),
            ReminderFrequency.entries.map { it.notificationsPerDay },
        )
        assertEquals(
            ReminderFrequency.ONCE,
            ReminderFrequency.fromStoredCount(99),
        )
    }

    @Test
    fun choosesTheNextSlotLaterToday() {
        val now = localTime(2026, 7, 27, 10, 30)

        assertEquals(
            localTime(2026, 7, 27, 14, 0),
            nextReminderAt(now, ReminderFrequency.THREE_TIMES),
        )
    }

    @Test
    fun rollsForwardToTomorrowAfterTheLastSlot() {
        val now = localTime(2026, 7, 27, 22, 0)

        assertEquals(
            localTime(2026, 7, 28, 9, 0),
            nextReminderAt(now, ReminderFrequency.FIVE_TIMES),
        )
    }

    @Test
    fun exactCurrentSlotIsNotScheduledTwice() {
        val now = localTime(2026, 7, 27, 9, 0)

        assertEquals(
            localTime(2026, 7, 27, 18, 0),
            nextReminderAt(now, ReminderFrequency.TWICE),
        )
    }

    private fun localTime(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): ZonedDateTime = ZonedDateTime.of(
        year,
        month,
        day,
        hour,
        minute,
        0,
        0,
        madrid,
    )
}
