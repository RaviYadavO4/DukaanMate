package com.portfolio.dukaanmate.data.local

import android.content.Context
import android.content.SharedPreferences
import com.portfolio.dukaanmate.ui.localization.AppLanguage

class LanguagePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dukaan_mate_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_DARK_THEME = "is_dark_theme"
    }

    fun getSavedLanguage(): AppLanguage {
        val savedCode = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
        return if (savedCode == AppLanguage.HINDI.code) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    // Save and load Dark Mode state
    fun isDarkTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, false)

    fun saveDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }
}
