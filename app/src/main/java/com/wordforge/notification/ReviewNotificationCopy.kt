package com.wordforge.notification

internal data class ReviewNotificationCopy(
    val title: String,
    val body: String,
)

/**
 * Keeps reminder copy intentionally independent of item content. A quiz may
 * show either side of a randomly flipped card, so including either side here
 * could reveal the answer before the review starts.
 */
internal fun reviewNotificationCopy(itemCount: Int): ReviewNotificationCopy {
    require(itemCount > 0) { "A review notification needs at least one item" }

    val title = if (itemCount == 1) {
        "1 item ready to review"
    } else {
        "$itemCount items ready to review"
    }
    return ReviewNotificationCopy(
        title = title,
        body = "Tap to start your review.",
    )
}
