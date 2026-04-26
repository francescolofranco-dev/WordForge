package com.wordforge.domain

import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object SpacedRepetition {
    const val MAX_TIER = 8
    const val MIN_TIER = 0

    val intervals: Array<Long> = arrayOf(
        1.hours.inWholeMilliseconds,
        6.hours.inWholeMilliseconds,
        12.hours.inWholeMilliseconds,
        24.hours.inWholeMilliseconds,
        2.days.inWholeMilliseconds,
        5.days.inWholeMilliseconds,
        10.days.inWholeMilliseconds,
        21.days.inWholeMilliseconds,
        30.days.inWholeMilliseconds,
    )

    private const val JITTER_FRACTION = 0.10

    fun onCorrect(currentTier: Int): Int {
        return if (currentTier < MAX_TIER) currentTier + 1 else currentTier
    }

    fun onIncorrect(currentTier: Int): Int {
        return if (currentTier > MIN_TIER) currentTier - 1 else currentTier
    }

    fun getIntervalMs(tier: Int): Long {
        return intervals[tier.coerceIn(MIN_TIER, MAX_TIER)]
    }

    /**
     * Interval for [tier] with ±10% random jitter so reviews don't pile up at the same minute.
     */
    fun nextDelayMs(tier: Int, random: Random = Random.Default): Long {
        val base = getIntervalMs(tier)
        val jitter = (base * JITTER_FRACTION).toLong()
        return base + random.nextLong(-jitter, jitter + 1)
    }
}
