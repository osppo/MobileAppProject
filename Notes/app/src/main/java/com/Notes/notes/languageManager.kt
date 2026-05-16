package com.Notes.notes

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {

    fun setLocale(context: Context, languageCode: String) {

        val locale = Locale(languageCode)

        Locale.setDefault(locale)

        val config = Configuration()

        config.setLocale(locale)

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )

        val sharedPref = context.getSharedPreferences(
            "language_pref",
            Context.MODE_PRIVATE
        )

        sharedPref.edit()
            .putString("language", languageCode)
            .apply()

        if (context is Activity) {
            context.recreate()
        }
    }

    fun getSavedLanguage(context: Context): String {

        val sharedPref = context.getSharedPreferences(
            "language_pref",
            Context.MODE_PRIVATE
        )

        return sharedPref.getString("language", "en") ?: "en"
    }
}