package com.wordforge.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeModeTest {
    @Test
    fun fromStorageKeyReturnsMatchingThemeMode() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageKey("system"))
        assertEquals(ThemeMode.LIGHT, ThemeMode.fromStorageKey("light"))
        assertEquals(ThemeMode.DARK, ThemeMode.fromStorageKey("dark"))
    }

    @Test
    fun fromStorageKeyFallsBackToSystemDefault() {
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageKey(null))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorageKey("unknown"))
    }

    @Test
    fun isDarkRespectsThemeMode() {
        assertEquals(true, ThemeMode.SYSTEM.isDark(systemInDarkTheme = true))
        assertEquals(false, ThemeMode.SYSTEM.isDark(systemInDarkTheme = false))
        assertEquals(false, ThemeMode.LIGHT.isDark(systemInDarkTheme = true))
        assertEquals(true, ThemeMode.DARK.isDark(systemInDarkTheme = false))
    }
}
