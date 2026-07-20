package com.wordforge.data

import android.content.Context
import com.wordforge.ui.theme.ThemeMode
import androidx.core.content.edit

class ThemePreferenceStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    var themeMode: ThemeMode
        get() = ThemeMode.fromStorageKey(preferences.getString(KEY_THEME_MODE, null))
        set(value) {
            preferences.edit {
                putString(KEY_THEME_MODE, value.storageKey)
            }
        }

    private companion object {
        const val PREFERENCES_NAME = "wordforge_preferences"
        const val KEY_THEME_MODE = "theme_mode"
    }
}
