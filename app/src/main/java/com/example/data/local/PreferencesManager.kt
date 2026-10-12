package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("scamshield_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            preferredLanguage = prefs.getString("preferred_language", "EN") ?: "EN",
            isDarkMode = prefs.getBoolean("is_dark_mode", true),
            storeFullMessages = prefs.getBoolean("store_full_messages", false),
            termsAcceptedVersion = prefs.getString("terms_version", "") ?: "",
            termsAcceptedTimestamp = prefs.getLong("terms_timestamp", 0L)
        )
    }

    fun updateLanguage(language: String) {
        prefs.edit().putString("preferred_language", language).apply()
        _settings.value = _settings.value.copy(preferredLanguage = language)
    }

    fun updateDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", isDark).apply()
        _settings.value = _settings.value.copy(isDarkMode = isDark)
    }

    fun updateStoreFullMessages(optIn: Boolean) {
        prefs.edit().putBoolean("store_full_messages", optIn).apply()
        _settings.value = _settings.value.copy(storeFullMessages = optIn)
    }

    fun acceptTerms(version: String = "1.0.0") {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putString("terms_version", version)
            .putLong("terms_timestamp", now)
            .apply()
        _settings.value = _settings.value.copy(
            termsAcceptedVersion = version,
            termsAcceptedTimestamp = now
        )
    }

    fun clearAllPreferences() {
        prefs.edit().clear().apply()
        _settings.value = UserSettings()
    }
}
