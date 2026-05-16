package com.Notes.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.Notes.notes.ui.theme.NotesTheme
import android.content.res.Configuration
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val languageCode =
            LanguageManager.getSavedLanguage(this)

        val locale = Locale(languageCode)

        Locale.setDefault(locale)

        val config = Configuration()

        config.setLocale(locale)

        resources.updateConfiguration(
            config,
            resources.displayMetrics
        )
        setContent {
            NotesTheme {
                NotesApp()
            }
        }
    }
}