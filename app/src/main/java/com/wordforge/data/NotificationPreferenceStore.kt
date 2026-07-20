package com.wordforge.data

import android.content.Context
import androidx.core.content.edit

/** Stores whether the app has explained why review notifications are useful. */
class NotificationPreferenceStore(context: Context) {
    private val preferences = context.getSharedPreferences(
        "wordforge_notification_preferences",
        Context.MODE_PRIVATE,
    )

    var hasShownEducation: Boolean
        get() = preferences.getBoolean(KEY_HAS_SHOWN_EDUCATION, false)
        set(value) {
            preferences.edit { putBoolean(KEY_HAS_SHOWN_EDUCATION, value) }
        }

    private companion object {
        const val KEY_HAS_SHOWN_EDUCATION = "has_shown_education"
    }
}
