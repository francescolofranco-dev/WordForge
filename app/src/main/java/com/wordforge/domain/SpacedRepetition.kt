package com.wordforge.domain

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object SpacedRepetition {
    const val MAX_TIER = 7
    const val MIN_TIER = 0

    val intervals: Array<Long> = arrayOf(
        1.hours.inWholeMilliseconds,
        6.hours.inWholeMilliseconds,
        12.hours.inWholeMilliseconds,
        24.hours.inWholeMilliseconds,
        2.days.inWholeMilliseconds,
        5.days.inWholeMilliseconds,
        14.days.inWholeMilliseconds,
        30.days.inWholeMilliseconds,
    )

    fun onCorrect(currentTier: Int): Int {
        return if (currentTier < MAX_TIER) currentTier + 1 else currentTier
    }

    fun onIncorrect(currentTier: Int): Int {
        return if (currentTier > MIN_TIER) currentTier - 1 else currentTier
    }

    fun getIntervalMs(tier: Int): Long {
        return intervals[tier.coerceIn(MIN_TIER, MAX_TIER)]
    }
}