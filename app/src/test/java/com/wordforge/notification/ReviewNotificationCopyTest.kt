package com.wordforge.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ReviewNotificationCopyTest {

    @Test
    fun singularCopyOnlyExposesTheReadyCount() {
        val copy = reviewNotificationCopy(itemCount = 1)

        assertEquals("1 item ready to review", copy.title)
        assertEquals("Tap to start your review.", copy.body)
    }

    @Test
    fun pluralCopyOnlyExposesTheReadyCount() {
        val copy = reviewNotificationCopy(itemCount = 5)

        assertEquals("5 items ready to review", copy.title)
        assertEquals("Tap to start your review.", copy.body)
    }

    @Test
    fun copyRejectsAnEmptyReview() {
        assertThrows(IllegalArgumentException::class.java) {
            reviewNotificationCopy(itemCount = 0)
        }
    }
}
