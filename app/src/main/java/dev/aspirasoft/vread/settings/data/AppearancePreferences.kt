package dev.aspirasoft.vread.settings.data

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import dev.aspirasoft.vread.R
import io.github.saifkhichi96.android.db.LocalDatabase
import javax.inject.Inject

class AppearancePreferences @Inject constructor(val db: LocalDatabase) {

    var theme: String
        get() = db.getOrDefault("Theme", THEME_DEFAULT)
        set(theme) = db.update("Theme", theme)

    var uiMode: String
        get() = db.getOrDefault("UiMode", MODE_LIGHT)
        set(uiMode) = db.update("UiMode", uiMode)

    fun updateUiMode() {
        AppCompatDelegate.setDefaultNightMode(
            when (MODE_DARK) {
                uiMode -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun updateTheme(activity: AppCompatActivity) {
        when (theme) {
            THEME_GODRIC -> activity.setTheme(R.style.GryffindorTheme)
            THEME_HELGA -> activity.setTheme(R.style.HufflepuffTheme)
            THEME_ROWENA -> activity.setTheme(R.style.RavenclawTheme)
            THEME_SALAZAR -> activity.setTheme(R.style.SlytherinTheme)
            else -> activity.setTheme(R.style.AppTheme)
        }
    }

    companion object {
        const val THEME_DEFAULT = "Default"
        const val THEME_GODRIC = "Gryffindor"
        const val THEME_HELGA = "Hufflepuff"
        const val THEME_ROWENA = "Ravenclaw"
        const val THEME_SALAZAR = "Slytherin"

        const val MODE_DARK = "Night"
        const val MODE_LIGHT = "Day"
    }

}