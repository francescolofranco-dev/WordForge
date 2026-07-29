package com.wordforge.data

/**
 * The number of grouped review reminders WordForge may show each day.
 * Slots are intentionally spread across normal waking hours.
 */
enum class ReminderFrequency(
    val notificationsPerDay: Int,
    val slotHours: List<Int>,
) {
    ONCE(
        notificationsPerDay = 1,
        slotHours = listOf(9),
    ),
    TWICE(
        notificationsPerDay = 2,
        slotHours = listOf(9, 18),
    ),
    THREE_TIMES(
        notificationsPerDay = 3,
        slotHours = listOf(9, 14, 19),
    ),
    FIVE_TIMES(
        notificationsPerDay = 5,
        slotHours = listOf(9, 12, 15, 18, 21),
    );

    companion object {
        fun fromStoredCount(value: Int): ReminderFrequency =
            entries.firstOrNull { it.notificationsPerDay == value } ?: ONCE
    }
}
