package com.wordforge.ui.theme

enum class ThemeMode(
    val storageKey: String,
    val label: String,
    val description: String
) {
    SYSTEM(
        storageKey = "system",
        label = "System default",
        description = "Follow your device appearance"
    ),
    LIGHT(
        storageKey = "light",
        label = "Light",
        description = "Use the bright WordForge theme"
    ),
    DARK(
        storageKey = "dark",
        label = "Dark",
        description = "Use the low-light WordForge theme"
    );

    fun isDark(systemInDarkTheme: Boolean): Boolean {
        return when (this) {
            SYSTEM -> systemInDarkTheme
            LIGHT -> false
            DARK -> true
        }
    }

    companion object {
        fun fromStorageKey(storageKey: String?): ThemeMode {
            return entries.firstOrNull { it.storageKey == storageKey } ?: SYSTEM
        }
    }
}
